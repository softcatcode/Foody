package com.softcat.foody.screens.scores

import com.arkivanov.mvikotlin.core.store.Store

interface ScoresStore: Store<ScoresStore.Intent, ScoresStore.State, Nothing> {

    sealed interface Intent {
        data class Remove(val recipeId: Int): Intent

        data class ChangeValue(val recipeId: Int, val newValue: Int): Intent

        data class ChangeFavouriteStatus(val recipeId: Int): Intent

        data class ChangeIsCookedFilter(val newValue: Boolean): Intent
    }

    data class State(
        val isCookedRequired: Boolean,
        val contentStatus: ContentStatus
    ) {

        sealed interface ContentStatus {
            data object Loading: ContentStatus

            data class Content(val scores: List<RecipeScoreModel>): ContentStatus
        }
    }
}