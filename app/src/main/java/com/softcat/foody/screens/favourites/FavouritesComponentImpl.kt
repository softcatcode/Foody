package com.softcat.foody.screens.favourites

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.softcat.domain.entities.FilterParams
import com.softcat.domain.entities.Recipe
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class FavouritesComponentImpl @AssistedInject constructor(
    private val storeFactory: FavouritesStoreFactory,
    @Assisted("context") private val componentContext: ComponentContext,
    @Assisted("searchRecipe") private val openSearchCallback: () -> Unit,
    @Assisted("recommend") private val openRecommendationsCallback: () -> Unit,
    @Assisted("details") private val openRecipeDetailsCallback: (Recipe) -> Unit,
): FavouritesComponent, ComponentContext by componentContext {

    private val store: FavouritesStore = instanceKeeper.getStore { storeFactory.create(componentContext.lifecycle) }
    private val scope = CoroutineScope(Dispatchers.Main)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<FavouritesStore.State> = store.stateFlow

    init {
        scope.launch {
            store.labels.collect(::labelCollector)
        }
    }

    private fun labelCollector(label: FavouritesStore.Label) {
        when (label) {
            is FavouritesStore.Label.OpenRecipeDetails -> openRecipeDetailsCallback(label.recipe)
        }
    }

    override fun removeFromFavourites(recipeId: Int) {
        Timber.i("${this::class.simpleName}: removeFromFavourites($recipeId)")
        store.accept(FavouritesStore.Intent.RemoveFromFavourites(recipeId))
    }

    override fun changeScore(newValue: Int) {
        Timber.i("${this::class.simpleName}: changeScore($newValue)")
        store.accept(FavouritesStore.Intent.ChangeScore(newValue))
    }

    override fun changeCookingTime(newValue: ClosedFloatingPointRange<Float>) {
        Timber.i("${this::class.simpleName}: changeCookingTime($newValue)")
        store.accept(FavouritesStore.Intent.ChangeCookingTime(newValue))
    }

    override fun changeCalories(newValue: ClosedFloatingPointRange<Float>) {
        Timber.i("${this::class.simpleName}: changeCalories($newValue)")
        store.accept(FavouritesStore.Intent.ChangeCalories(newValue))
    }

    override fun changeIsCookedStatus(newValue: FilterParams.TripleChoice) {
        Timber.i("${this::class.simpleName}: changeIsCookedStatus($newValue)")
        store.accept(FavouritesStore.Intent.ChangeIsCooked(newValue))
    }

    override fun tagClicked(name: String) {
        Timber.i("${this::class.simpleName}: tagClicked($name)")
        store.accept(FavouritesStore.Intent.TagClicked(name))
    }

    override fun ingredientClicked(name: String) {
        Timber.i("${this::class.simpleName}: ingredientClicked($name)")
        store.accept(FavouritesStore.Intent.IngredientClicked(name))
    }

    override fun openRecommendationScreen() {
        Timber.i("${this::class.simpleName}: openRecommendationScreen()")
        openRecommendationsCallback()
    }

    override fun openSearchScreen() {
        Timber.i("${this::class.simpleName}: openSearchScreen()")
        openSearchCallback()
    }

    override fun openRecipeDetailsScreen(recipeId: Int) {
        Timber.i("${this::class.simpleName}: openRecipeDetailsScreen($recipeId)")
        store.accept(FavouritesStore.Intent.OpenRecipeDetails(recipeId))
    }

    override fun expandFiltersSheet() {
        Timber.i("${this::class.simpleName}: expandFiltersSheet()")
        store.accept(FavouritesStore.Intent.ExpandFiltersSheet)
    }

    override fun hideFiltersSheet() {
        Timber.i("${this::class.simpleName}: hideFiltersSheet()")
        store.accept(FavouritesStore.Intent.HideFiltersSheet)
    }

    override fun resetFilters() {
        Timber.i("${this::class.simpleName}: resetFilters()")
        store.accept(FavouritesStore.Intent.ResetFilters)
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("context") componentContext: ComponentContext,
            @Assisted("searchRecipe") openSearchCallback: () -> Unit,
            @Assisted("recommend")  openRecommendationsCallback: () -> Unit,
            @Assisted("details") openRecipeDetailsCallback: (Recipe) -> Unit,
        ): FavouritesComponentImpl
    }
}