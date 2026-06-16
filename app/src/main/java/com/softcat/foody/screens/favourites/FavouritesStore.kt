package com.softcat.foody.screens.favourites

import com.arkivanov.mvikotlin.core.store.Store
import com.softcat.domain.entities.FilterParams
import com.softcat.domain.entities.FilterParams.Companion.MAX_CALORIES
import com.softcat.domain.entities.FilterParams.Companion.MAX_DURATION
import com.softcat.domain.entities.FilterParams.TripleChoice
import com.softcat.domain.entities.Recipe
import com.softcat.foody.common.RecipeModel

interface FavouritesStore: Store<FavouritesStore.Intent, FavouritesStore.State, FavouritesStore.Label> {

    sealed interface Intent {
        data class RemoveFromFavourites(val recipeId: Int): Intent

        data class ChangeScore(val newValue: Int): Intent

        data class ChangeCookingTime(val newValue: ClosedFloatingPointRange<Float>): Intent

        data class ChangeCalories(val newValue: ClosedFloatingPointRange<Float>): Intent

        data class ChangeIsCooked(val newValue: FilterParams.TripleChoice): Intent

        data class TagClicked(val name: String): Intent

        data class IngredientClicked(val name: String): Intent

        data class OpenRecipeDetails(val recipeId: Int): Intent

        data object ExpandFiltersSheet: Intent

        data object HideFiltersSheet: Intent

        data object ResetFilters: Intent
    }

    data class State(
        val filtersStatus: FiltersSheetState,
        val contentStatus: ContentStatus
    ) {
        data class FiltersSheetState(
            val filterParameters: FilterParams,
            val suggestedTags: List<String>,
            val suggestedIngredients: List<String>,
            val expanded: Boolean
        )

        sealed interface ContentStatus {
            data class RecipeList(val recipes: List<RecipeModel>): ContentStatus

            data object Empty: ContentStatus

            data object UserIsAbsent: ContentStatus

            data object Loading: ContentStatus
        }
    }

    sealed interface Label {
        data class OpenRecipeDetails(val recipe: Recipe): Label
    }
}