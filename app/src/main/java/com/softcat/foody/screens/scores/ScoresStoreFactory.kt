package com.softcat.foody.screens.scores

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnStart
import com.arkivanov.essenty.lifecycle.doOnStop
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.softcat.domain.entities.Recipe
import com.softcat.domain.entities.Score
import com.softcat.domain.usecases.FavouritesUseCase
import com.softcat.domain.usecases.RecipeUseCase
import com.softcat.domain.usecases.ScoreUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class ScoresStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val favouritesUseCase: FavouritesUseCase,
    private val scoreUseCase: ScoreUseCase,
    private val recipesUseCase: RecipeUseCase,
) {
    fun create(userId: String, lifecycle: Lifecycle): ScoresStore =
        object:
            ScoresStore, Store<ScoresStore.Intent, ScoresStore.State, Nothing>
        by
            storeFactory.create(
                name = this::class.simpleName,
                initialState = ScoresStore.State(
                    isCookedRequired = false,
                    contentStatus = ScoresStore.State.ContentStatus.Loading,
                ),
                executorFactory = { ScoresExecutor(userId, lifecycle) },
                reducer = ScoresReducer
            ) {}

    sealed interface Msg {
        data class ScoresLoaded(val scores: List<RecipeScoreModel>): Msg

        data class IsCookedFilterChanged(val newValue: Boolean): Msg
    }

    private inner class ScoresExecutor(
        private val userId: String,
        lifecycle: Lifecycle
    ): CoroutineExecutor<ScoresStore.Intent, Nothing, ScoresStore.State, Msg, Nothing>() {

        private var scores: List<Score>? = null
        private var recipes: List<Recipe>? = null
        private var favouriteIds: Set<Int>? = null

        private var scoresCollectingJob: Job? = null
        private var favouritesCollectingJob: Job? = null

        init {
            lifecycle.doOnStart {
                scoresCollectingJob = scope.launch(Dispatchers.IO) {
                    scoreUseCase.observe(userId).collect {
                        withContext(Dispatchers.Main) { updateScores(it) }
                    }
                }

                favouritesCollectingJob = scope.launch(Dispatchers.IO) {
                    favouritesUseCase.observeFavouriteIds(userId).collect {
                        withContext(Dispatchers.Main) { updateFavourites(it) }
                    }
                }
            }
            lifecycle.doOnStop {
                scoresCollectingJob?.cancel()
                favouritesCollectingJob?.cancel()

                scoresCollectingJob = null
                favouritesCollectingJob = null
            }
        }

        private fun updateScores(newScores: List<Score>) {
            scores = newScores
            val recipeIds = newScores.map { it.recipeId }
            scope.launch(Dispatchers.IO) {
                recipes = recipesUseCase.get(recipeIds)
                val models = mapToScoresModels(scores, recipes, favouriteIds, state().isCookedRequired)
                withContext(Dispatchers.Main) {
                    dispatch(Msg.ScoresLoaded(models))
                }
            }
        }

        private fun updateFavourites(newFavouriteIds: Set<Int>) {
            favouriteIds = newFavouriteIds
            val models = mapToScoresModels(scores, recipes, favouriteIds, state().isCookedRequired)
            dispatch(Msg.ScoresLoaded(models))
        }

        override fun executeIntent(intent: ScoresStore.Intent) {
            Timber.i("${this::class.simpleName}: Intent is obtained: $intent")
            when (intent) {

                is ScoresStore.Intent.ChangeFavouriteStatus -> changeIsFavourite(intent.recipeId)

                is ScoresStore.Intent.ChangeValue -> {
                    scope.launch(Dispatchers.IO) {
                        scoreUseCase.save(userId, intent.recipeId, intent.newValue)
                    }
                }

                is ScoresStore.Intent.Remove -> {
                    scope.launch(Dispatchers.IO) {
                        scoreUseCase.remove(userId, intent.recipeId)
                    }
                }

                is ScoresStore.Intent.ChangeIsCookedFilter -> {
                    dispatch(Msg.IsCookedFilterChanged(intent.newValue))
                    val scores = mapToScoresModels(scores, recipes, favouriteIds, intent.newValue)
                    dispatch(Msg.ScoresLoaded(scores))
                }
            }
        }

        private fun changeIsFavourite(recipeId: Int) {
            val content = state().contentStatus as? ScoresStore.State.ContentStatus.Content ?: return
            val scoreModel = content.scores.find { recipeId == it.recipeId } ?: return

            scope.launch(Dispatchers.IO) {
                if (scoreModel.isFavourite) {
                    favouritesUseCase.remove(userId, recipeId)
                } else {
                    favouritesUseCase.add(userId, recipeId)
                }
            }
        }
    }

    private object ScoresReducer: Reducer<ScoresStore.State, Msg> {
        override fun ScoresStore.State.reduce(msg: Msg): ScoresStore.State {
            Timber.i("${this::class.simpleName}: Message is obtained: $msg")
            return when (msg) {
                is Msg.ScoresLoaded ->
                    copy(contentStatus = ScoresStore.State.ContentStatus.Content(msg.scores))

                is Msg.IsCookedFilterChanged ->
                    copy(isCookedRequired = msg.newValue)
            }
        }
    }

    private fun mapToScoresModels(
        scores: List<Score>?,
        recipes: List<Recipe>?,
        favouriteIds: Set<Int>?,
        isCookedRequired: Boolean
    ): List<RecipeScoreModel> {
        return scores
            .orEmpty()
            .mapNotNull { score ->
                val recipe = recipes?.find { score.recipeId == it.id }
                recipe?.let {
                    if (isCookedRequired && !recipe.isCooked)
                        null
                    else {
                        RecipeScoreModel(
                            id = score.recipeId,
                            recipeId = score.recipeId,
                            score = score.value,
                            name = recipe.name,
                            description = recipe.description,
                            isFavouriteVisible = favouriteIds != null,
                            isFavourite = favouriteIds?.contains(recipe.id) ?: false,
                            date = score.date.formatAsDate()
                        )
                    }
                }
            }
    }

    private fun Calendar.formatAsDate(): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return dateFormat.format(this.time)
    }
}