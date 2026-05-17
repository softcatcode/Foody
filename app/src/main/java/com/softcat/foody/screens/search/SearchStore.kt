package com.softcat.foody.screens.search

import com.arkivanov.mvikotlin.core.store.Store
import com.softcat.domain.entities.FilterParams
import com.softcat.domain.entities.Recipe
import com.softcat.foody.common.RecipeModel

interface SearchStore: Store<SearchStore.Intent, SearchStore.State, SearchStore.Label> {

    data class State(
        val searchQuery: String,
        val filtersState: FiltersSheetState,
        val searchStatus: SearchStatus
    ) {

        data class FiltersSheetState(
            val filterParameters: FilterParams,
            val visibleTags: List<String>,
            val visibleIngredients: List<String>,
            val expanded: Boolean
        )

        sealed interface SearchStatus {

            data object Loading: SearchStatus

            data object Initial: SearchStatus

            data object Empty: SearchStatus

            data class Content(val recipes: List<RecipeModel>): SearchStatus
        }
    }

    sealed interface Intent {
        data class Search(val query: String): Intent

        data class AddToFavourites(val recipeId: Int): Intent

        data class RemoveFromFavourites(val recipeId: Int): Intent

        data class ChangeScore(val newValue: Int): Intent

        data class ChangeCookingTime(val newValue: ClosedFloatingPointRange<Float>): Intent

        data class ChangeCalories(val newValue: ClosedFloatingPointRange<Float>): Intent

        data class ChangeIsCooked(val newValue: FilterParams.TripleChoice): Intent

        data class TagClicked(val name: String): Intent

        data class IngredientClicked(val name: String): Intent

        data object ExpandFiltersSheet: Intent

        data object HideFiltersSheet: Intent

        data object ResetFilters: Intent

        data class ChangeSearchQuery(val newValue: String): Intent

        data class OpenRecipeDetails(val recipeId: Int): Intent
    }

    sealed interface Label {
        data class OpenRecipeDetails(val recipe: Recipe): Label
    }
}