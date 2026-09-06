package com.softcat.foody.screens.recomend

import com.arkivanov.mvikotlin.core.store.Store
import com.softcat.domain.entities.Recipe
import com.softcat.domain.entities.RecipeTag

interface RecommendStore : Store<RecommendStore.Intent, RecommendStore.State, RecommendStore.Label> {
    sealed interface Intent {
        data class RemoveIngredient(val name: String) : Intent

        data class AddTag(val elem: RecipeTag) : Intent

        data class RemoveTag(val name: String) : Intent

        data class ChangeMaxAbsentIngredients(val newValue: Int) : Intent

        data class ChangeFavouriteStatus(val recipeId: Int) : Intent

        data class OpenRecipeDetails(val id: Int): Intent

        data class SearchTag(val query: String): Intent

        data class ChangeSearchTagQuery(val vewValue: String): Intent

        data object Recommend : Intent

        data object ShowAddRequiredTagDialog : Intent

        data object HideDialog : Intent
    }

    data class State(
        val ingredients: List<String>,
        val tags: List<String>,
        val maxAbsentIngredients: Int,
        val tagDialogState: SelectTagDialogState,
        val resultStatus: RecommendationStatus,
    ) {

        sealed interface SelectTagDialogState {
            data object Hidden: SelectTagDialogState

            data class Shown(
                val query: String,
                val searchResult: List<RecipeTag>
            ): SelectTagDialogState
        }

        sealed interface RecommendationStatus {
            data object Initial : RecommendationStatus

            data object Loading : RecommendationStatus

            data class Content(
                val recipes: List<RecipeRecommendationModel>,
            ) : RecommendationStatus
        }
    }

    sealed interface Label {
        data class OpenRecipeDetails(val recipe: Recipe): Label

        data class Error(val error: Throwable): Label
    }
}
