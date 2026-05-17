package com.softcat.foody.screens.details

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnStart
import com.arkivanov.essenty.lifecycle.doOnStop
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.softcat.domain.entities.Recipe
import com.softcat.domain.entities.User
import com.softcat.domain.usecases.FavouritesUseCase
import com.softcat.domain.usecases.RecipeUseCase
import com.softcat.domain.usecases.ScoreUseCase
import com.softcat.domain.usecases.UserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class DetailsStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val favouritesUseCase: FavouritesUseCase,
    private val userUseCase: UserUseCase,
    private val scoreUseCase: ScoreUseCase,
    private val recipeUseCase: RecipeUseCase,
) {

    fun create(recipe: Recipe, lifecycle: Lifecycle): DetailsStore =
        object: DetailsStore, Store<DetailsStore.Intent, DetailsStore.State, Nothing> by
        storeFactory.create(
            name = this::class.simpleName,
            initialState = DetailsStore.State(
                recipe = recipe,
                stepNumber = 1,
                scoring = DetailsStore.State.UserScoring.UserIsAbsent
            ),
            executorFactory = { DetailsExecutor(recipe.id, lifecycle) },
            reducer = DetailsReducer
        ) {}

    sealed interface Msg {
        data class ChangeIdFavourite(val newValue: Boolean): Msg

        data class ChangeIsCooked(val newValue: Boolean): Msg

        data class ChangeScore(val newValue: Int): Msg

        data object NextStep: Msg
        data object PreviousStep: Msg
    }

    private inner class DetailsExecutor(
        private val recipeId: Int,
        lifecycle: Lifecycle
    ): CoroutineExecutor<DetailsStore.Intent, Nothing, DetailsStore.State, Msg, Nothing>() {

        private var userId: String? = null

        private var favouritesCollectingJob: Job? = null
        private var scoreCollectJob: Job? = null
        private var isCookedCollectJob: Job? = null
        private var userCollectJob: Job? = null

        init {
            lifecycle.doOnStart {
                userCollectJob = scope.launch {
                    userUseCase.observeLastEnteredUser().collect(::userCollector)
                }
                isCookedCollectJob = scope.launch {
                    recipeUseCase.observeIsCooked(recipeId).collect {
                        withContext(Dispatchers.Main) {
                            dispatch(Msg.ChangeIsCooked(it))
                        }
                    }
                }
            }
            lifecycle.doOnStop {
                favouritesCollectingJob?.cancel()
                scoreCollectJob?.cancel()
                isCookedCollectJob?.cancel()
                userCollectJob?.cancel()
            }
        }

        private fun userCollector(user: User?) {
            userId = user?.id
            userId?.let { userId ->
                favouritesCollectingJob = scope.launch(Dispatchers.IO) {
                    favouritesUseCase.observeIsFavourite(userId, recipeId).collect {
                        withContext(Dispatchers.Main) {
                            dispatch(Msg.ChangeIdFavourite(it))
                        }
                    }
                }
                scoreCollectJob = scope.launch(Dispatchers.IO) {
                    scoreUseCase.observeScoreValue(userId, recipeId).collect {
                        withContext(Dispatchers.Main) {
                            dispatch(Msg.ChangeScore(it))
                        }
                    }
                }
            }
        }

        override fun executeIntent(intent: DetailsStore.Intent) {
            Timber.i("${this::class.simpleName}: Intent is obtained: $intent")
            when (intent) {

                DetailsStore.Intent.AddToFavourites -> userId?.let {
                    scope.launch(Dispatchers.IO) {
                        favouritesUseCase.add(it, recipeId)
                    }
                }

                is DetailsStore.Intent.ChangeIsCooked -> {
                    scope.launch {
                        recipeUseCase.setIsCooked(recipeId, intent.newValue)
                    }
                }

                DetailsStore.Intent.NextStep -> {
                    if (state().stepNumber < state().recipe.steps.size)
                        dispatch(Msg.NextStep)
                }
                DetailsStore.Intent.PreviousStep -> {
                    if (state().stepNumber > 1)
                        dispatch(Msg.PreviousStep)
                }

                DetailsStore.Intent.RemoveFromFavourites -> userId?.let {
                    scope.launch(Dispatchers.IO) {
                        favouritesUseCase.remove(it, recipeId)
                    }
                }

                is DetailsStore.Intent.UpdateScore -> userId?.let {
                    scope.launch(Dispatchers.IO) {
                        scoreUseCase.save(it, recipeId, intent.newValue)
                    }
                }

                DetailsStore.Intent.RemoveScore -> userId?.let {
                    scope.launch(Dispatchers.IO) {
                        scoreUseCase.remove(it, recipeId)
                    }
                }
            }
        }
    }

    private object DetailsReducer: Reducer<DetailsStore.State, Msg> {

        override fun DetailsStore.State.reduce(msg: Msg): DetailsStore.State {
            Timber.i("${this::class.simpleName}: Message is obtained: $msg")

            return when (msg) {
                is Msg.ChangeIdFavourite -> {
                    if (scoring is DetailsStore.State.UserScoring.UserAuthorized)
                        copy(scoring = scoring.copy(isFavourite = msg.newValue))
                    else
                        copy(
                            scoring = DetailsStore.State.UserScoring.UserAuthorized(
                                score = null, isFavourite = msg.newValue
                            )
                        )
                }
                is Msg.ChangeIsCooked -> copy(recipe = recipe.copy(isCooked = msg.newValue))

                is Msg.ChangeScore -> {
                    if (scoring is DetailsStore.State.UserScoring.UserAuthorized)
                        copy(scoring = scoring.copy(score = msg.newValue))
                    else
                        this
                }

                Msg.NextStep -> {
                    if (stepNumber < recipe.steps.size)
                        copy(stepNumber = stepNumber + 1)
                    else
                        this
                }

                Msg.PreviousStep -> {
                    if (stepNumber > 1)
                        copy(stepNumber = stepNumber - 1)
                    else
                        this
                }
            }
        }
    }
}