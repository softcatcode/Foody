package com.softcat.foody.screens.details

import com.arkivanov.mvikotlin.core.store.Store
import com.softcat.domain.entities.Recipe

interface DetailsStore: Store<DetailsStore.Intent, DetailsStore.State, Nothing> {

    sealed interface Intent {
        data object AddToFavourites: Intent
        data object RemoveFromFavourites: Intent

        data class UpdateScore(val newValue: Int): Intent
        data object RemoveScore: Intent

        data class ChangeIsCooked(val newValue: Boolean): Intent

        data object NextStep: Intent
        data object PreviousStep: Intent
    }

    data class State(
        val recipe: Recipe,
        val stepNumber: Int,
        val scoring: UserScoring
    ) {
        sealed interface UserScoring {
            data class UserAuthorized(
                val score: Int?,
                val isFavourite: Boolean
            ): UserScoring

            data object UserIsAbsent: UserScoring
        }
    }
}