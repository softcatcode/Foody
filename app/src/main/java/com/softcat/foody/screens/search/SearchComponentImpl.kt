package com.softcat.foody.screens.search

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

class SearchComponentImpl @AssistedInject constructor(
    storeFactory: SearchStoreFactory,
    @Assisted("context") private val componentContext: ComponentContext,
    @Assisted("details") private val openRecipeDetailsCallback: (Recipe) -> Unit,
): SearchComponent, ComponentContext by componentContext {

    private val store: SearchStore = instanceKeeper.getStore { storeFactory.create(componentContext.lifecycle) }
    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        scope.launch {
            store.labels.collect(::labelCollector)
        }
    }

    private fun labelCollector(label: SearchStore.Label) {
        when (label) {
            is SearchStore.Label.OpenRecipeDetails -> openRecipeDetailsCallback(label.recipe)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<SearchStore.State> = store.stateFlow

    override fun search(query: String) {
        Timber.i("${this::class.simpleName} searchRecipe($query)")
        store.accept(SearchStore.Intent.Search(query))
    }

    override fun addToFavourites(recipeId: Int) {
        Timber.i("${this::class.simpleName} addToFavourites($recipeId)")
        store.accept(SearchStore.Intent.AddToFavourites(recipeId))
    }

    override fun removeFromFavourites(recipeId: Int) {
        Timber.i("${this::class.simpleName} addToFavourites($recipeId)")
        store.accept(SearchStore.Intent.RemoveFromFavourites(recipeId))
    }

    override fun openRecipeDetails(recipeId: Int) {
        Timber.i("${this::class.simpleName} openRecipeDetails($recipeId)")
        store.accept(SearchStore.Intent.OpenRecipeDetails(recipeId))
    }

    override fun changeScore(newValue: Int) {
        Timber.i("${this::class.simpleName}: changeScore($newValue)")
        store.accept(SearchStore.Intent.ChangeScore(newValue))
    }

    override fun changeCookingTime(newValue: ClosedFloatingPointRange<Float>) {
        Timber.i("${this::class.simpleName}: changeCookingTime($newValue)")
        store.accept(SearchStore.Intent.ChangeCookingTime(newValue))
    }

    override fun changeCalories(newValue: ClosedFloatingPointRange<Float>) {
        Timber.i("${this::class.simpleName}: changeCalories($newValue)")
        store.accept(SearchStore.Intent.ChangeCalories(newValue))
    }

    override fun changeIsCookedStatus(newValue: FilterParams.TripleChoice) {
        Timber.i("${this::class.simpleName}: changeIsCookedStatus($newValue)")
        store.accept(SearchStore.Intent.ChangeIsCooked(newValue))
    }

    override fun tagClicked(name: String) {
        Timber.i("${this::class.simpleName}: tagClicked($name)")
        store.accept(SearchStore.Intent.TagClicked(name))
    }

    override fun ingredientClicked(name: String) {
        Timber.i("${this::class.simpleName}: ingredientClicked($name)")
        store.accept(SearchStore.Intent.IngredientClicked(name))
    }

    override fun changeQuery(newValue: String) {
        Timber.i("${this::class.simpleName} changeQuery($newValue)")
        store.accept(SearchStore.Intent.ChangeSearchQuery(newValue))
    }

    override fun expandFiltersSheet() {
        Timber.i("${this::class.simpleName} expandFiltersSheet()")
        store.accept(SearchStore.Intent.ExpandFiltersSheet)
    }

    override fun hideFiltersSheet() {
        Timber.i("${this::class.simpleName} hideFiltersSheet()")
        store.accept(SearchStore.Intent.HideFiltersSheet)
    }

    override fun resetFilters() {
        Timber.i("${this::class.simpleName} resetFilters()")
        store.accept(SearchStore.Intent.ResetFilters)
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("context") componentContext: ComponentContext,
            @Assisted("details") openRecipeDetailsCallback: (Recipe) -> Unit,
        ): SearchComponentImpl
    }
}