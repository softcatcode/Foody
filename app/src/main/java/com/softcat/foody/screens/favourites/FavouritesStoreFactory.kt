package com.softcat.foody.screens.favourites

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnStart
import com.arkivanov.essenty.lifecycle.doOnStop
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.softcat.data.filter
import com.softcat.domain.entities.FilterParams
import com.softcat.domain.entities.Recipe
import com.softcat.domain.entities.User
import com.softcat.domain.usecases.FavouritesUseCase
import com.softcat.domain.usecases.IngredientUseCase
import com.softcat.domain.usecases.RecipeTagUseCase
import com.softcat.domain.usecases.ScoreUseCase
import com.softcat.domain.usecases.UserUseCase
import com.softcat.foody.common.RecipeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class FavouritesStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val favouritesUseCase: FavouritesUseCase,
    private val scoreUseCase: ScoreUseCase,
    private val userUseCase: UserUseCase,
    private val ingredientUseCase: IngredientUseCase,
    private val tagsUseCase: RecipeTagUseCase
) {
    fun create(lifecycle: Lifecycle): FavouritesStore = object:
        FavouritesStore, Store<FavouritesStore.Intent, FavouritesStore.State, FavouritesStore.Label> by
        storeFactory.create(
            name = this::class.simpleName,
            initialState = FavouritesStore.State(
                contentStatus = FavouritesStore.State.ContentStatus.Loading,
                filtersStatus = FavouritesStore.State.FiltersSheetState(
                    filterParameters = FilterParams(),
                    visibleTags = emptyList(),
                    visibleIngredients = emptyList(),
                    expanded = false
                ),
            ),
            bootstrapper = FavouriteBootstrapper(),
            executorFactory = { FavouriteExecutor(lifecycle) },
            reducer = FavouriteReducer
        ) {}

    private var user: User? = null

    private inner class FavouriteBootstrapper: CoroutineBootstrapper<Action>() {

        override fun invoke() {
            scope.launch(Dispatchers.IO) {
                val ingredients = ingredientUseCase.getIngredients(1000).map { it.name }
                withContext(Dispatchers.Main) {
                    dispatch(Action.IngredientsLoaded(ingredients))
                }
                val tags = tagsUseCase.getTags(1000).map { it.name }
                withContext(Dispatchers.Main) {
                    dispatch(Action.TagsLoaded(tags))
                }
            }
        }
    }

    private sealed interface Action {
        data class TagsLoaded(val tags: List<String>): Action

        data class IngredientsLoaded(val ingredients: List<String>): Action
    }

    private inner class FavouriteExecutor(
        lifecycle: Lifecycle
    ): CoroutineExecutor<FavouritesStore.Intent, Action, FavouritesStore.State, Msg, FavouritesStore.Label>() {

        private var scores: Map<Int, Int>? = null
        private var favourites: List<Recipe>? = null

        private var scoresCollectingJob: Job? = null
        private var favouritesCollectingJob: Job? = null
        private var userCollectingJob: Job? = null

        init {
            lifecycle.doOnStart {
                userCollectingJob = scope.launch {
                    userUseCase.observeLastEnteredUser().collect(::lastEnteredUserCollector)
                }
            }
            lifecycle.doOnStop {
                scoresCollectingJob?.cancel()
                favouritesCollectingJob?.cancel()
                userCollectingJob?.cancel()

                scoresCollectingJob = null
                favouritesCollectingJob = null
                userCollectingJob = null
            }
        }

        override fun executeAction(action: Action) {
            Timber.i("${this::class.simpleName}: Action is obtained: $action")
            when (action) {
                is Action.IngredientsLoaded -> dispatch(Msg.IngredientsLoaded(action.ingredients))
                is Action.TagsLoaded -> dispatch(Msg.TagsLoaded(action.tags))
            }
        }

        override fun executeIntent(intent: FavouritesStore.Intent) {
            Timber.i("${this::class.simpleName}: Intent is obtained: $intent")
            when (intent) {
                is FavouritesStore.Intent.RemoveFromFavourites -> user?.id?.let { userId ->
                    scope.launch(Dispatchers.IO) {
                        favouritesUseCase.remove(userId, intent.recipeId)
                    }
                }

                is FavouritesStore.Intent.ChangeScore -> {
                    val params = state().filtersStatus.filterParameters.copy(minScore = intent.newValue)
                    updateFilterParams(params)
                }

                is FavouritesStore.Intent.ChangeCookingTime -> {
                    val params = state().filtersStatus.filterParameters.copy(duration = intent.newValue)
                    updateFilterParams(params)
                }

                is FavouritesStore.Intent.ChangeCalories -> {
                    val params = state().filtersStatus.filterParameters.copy(calories = intent.newValue)
                    updateFilterParams(params)
                }

                is FavouritesStore.Intent.ChangeIsCooked -> {
                    val params = state().filtersStatus.filterParameters.copy(isCooked = intent.newValue)
                    updateFilterParams(params)
                }

                FavouritesStore.Intent.ExpandFiltersSheet -> dispatch(Msg.ExpandFiltersSheet)

                FavouritesStore.Intent.HideFiltersSheet -> dispatch(Msg.HideFiltersSheet)

                is FavouritesStore.Intent.TagClicked -> tagClicked(intent.name)

                is FavouritesStore.Intent.IngredientClicked -> ingredientClicked(intent.name)

                FavouritesStore.Intent.ResetFilters -> dispatch(Msg.ResetFilters)

                is FavouritesStore.Intent.OpenRecipeDetails -> {
                    val recipe = favourites?.find { it.id == intent.recipeId }
                    recipe?.let { publish(FavouritesStore.Label.OpenRecipeDetails(it)) }
                }
            }
        }

        private suspend fun lastEnteredUserCollector(newUser: User?) {
            user = newUser
            val userId = user?.id
            favouritesCollectingJob?.cancel()
            scoresCollectingJob?.cancel()

            if (userId == null) {
                withContext(Dispatchers.Main) {
                    dispatch(Msg.UserIsAbsent)
                }
                favouritesCollectingJob = null
                scoresCollectingJob = null
            } else {
                favouritesCollectingJob = scope.launch(Dispatchers.IO) {
                    favouritesUseCase.observe(userId).collect {
                        withContext(Dispatchers.Main) { updateFavourites(it) }
                    }
                }
                scoresCollectingJob = scope.launch(Dispatchers.IO) {
                    scoreUseCase.observeScoresMap(userId).collect {
                        withContext(Dispatchers.Main) { updateScores(it) }
                    }
                }
            }
        }

        private fun updateFilterParams(params: FilterParams) {
            dispatch(Msg.UpdateFilterParams(params))
            val recipes = mapToRecipeModels(favourites, scores, params)
            dispatch(Msg.FavouritesLoaded(recipes))
        }

        private fun tagClicked(name: String) {
            val visibleTags = state().filtersStatus.visibleTags
            val reqTags = state().filtersStatus.filterParameters.reqTags.toMutableList()
            val tag = visibleTags.find { it == name } ?: return
            if (tag in reqTags)
                reqTags.remove(tag)
            else
                reqTags.add(tag)
            val params = state().filtersStatus.filterParameters.copy(
                reqTags = reqTags
            )
            updateFilterParams(params)
        }

        private fun ingredientClicked(name: String) {
            val visibleIngredients = state().filtersStatus.visibleIngredients
            val reqIngredients = state().filtersStatus.filterParameters.reqIngredients.toMutableList()
            val ingredient = visibleIngredients.find { it == name } ?: return
            if (ingredient in reqIngredients)
                reqIngredients.remove(ingredient)
            else
                reqIngredients.add(ingredient)
            val params = state().filtersStatus.filterParameters.copy(
                reqIngredients = reqIngredients
            )
            updateFilterParams(params)
        }

        private fun updateFavourites(recipes: List<Recipe>) {
            favourites = recipes
            if (recipes.isEmpty()) {
                dispatch(Msg.FavouritesIsEmpty)
            } else {
                val recipes = mapToRecipeModels(
                    recipes = favourites.orEmpty(),
                    scores = scores,
                    filterParams = state().filtersStatus.filterParameters
                )
                dispatch(Msg.FavouritesLoaded(recipes))
            }
        }

        private fun updateScores(newScores: Map<Int, Int>) {
            scores = newScores
            if (state().contentStatus is FavouritesStore.State.ContentStatus.RecipeList) {
                val recipes = mapToRecipeModels(
                    recipes = favourites.orEmpty(),
                    scores = newScores,
                    filterParams = state().filtersStatus.filterParameters
                )
                dispatch(Msg.FavouritesLoaded(recipes))
            }
        }
    }

    private object FavouriteReducer: Reducer<FavouritesStore.State, Msg> {
        override fun FavouritesStore.State.reduce(msg: Msg): FavouritesStore.State {
            Timber.i("${this::class.simpleName}: Message is obtained: $msg")
            return when (msg) {
                is Msg.UpdateFilterParams ->
                    copy(filtersStatus = filtersStatus.copy(filterParameters = msg.params))

                is Msg.IngredientsLoaded ->
                    copy(filtersStatus = filtersStatus.copy(visibleIngredients = msg.ingredients))

                is Msg.TagsLoaded ->
                    copy(filtersStatus = filtersStatus.copy(visibleTags = msg.tags))

                Msg.ExpandFiltersSheet -> copy(filtersStatus = filtersStatus.copy(expanded = true))

                Msg.HideFiltersSheet -> copy(filtersStatus = filtersStatus.copy(expanded = false))

                Msg.UserIsAbsent -> copy(contentStatus = FavouritesStore.State.ContentStatus.UserIsAbsent)

                Msg.FavouritesIsEmpty -> copy(contentStatus = FavouritesStore.State.ContentStatus.Empty)

                Msg.ResetFilters -> copy(filtersStatus = filtersStatus.copy(filterParameters = FilterParams()))

                is Msg.FavouritesLoaded ->
                    copy(contentStatus = FavouritesStore.State.ContentStatus.RecipeList(msg.recipes))
            }
        }
    }

    private sealed interface Msg {
        data class UpdateFilterParams(val params: FilterParams): Msg

        data class FavouritesLoaded(val recipes: List<RecipeModel>): Msg

        data class TagsLoaded(val tags: List<String>): Msg

        data class IngredientsLoaded(val ingredients: List<String>): Msg

        data object ExpandFiltersSheet: Msg

        data object HideFiltersSheet: Msg

        data object ResetFilters: Msg

        data object UserIsAbsent: Msg

        data object FavouritesIsEmpty: Msg
    }

    fun mapToRecipeModels(
        recipes: List<Recipe>?,
        scores: Map<Int, Int>?,
        filterParams: FilterParams
    ): List<RecipeModel> {
        return recipes
            .orEmpty()
            .filter(filterParams, scores?.map { it.key to it.value.toFloat() }?.toMap())
            .map { recipe ->
                RecipeModel(
                    id = recipe.id,
                    favouriteButtonVisible = true,
                    isFavourite = true,
                    ingredients = recipe.ingredients.map { it.name },
                    name = recipe.name,
                    description = recipe.description,
                    score = (scores?.get(recipe.id) ?: 0).toString(),
                    scoreVisible = scores?.contains(recipe.id) ?: false
                )
            }
    }
}