package com.softcat.foody.screens.fridge

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.arkivanov.mvikotlin.core.store.Store
import com.softcat.domain.entities.Ingredient

interface FridgeStore: Store<FridgeStore.Intent, FridgeStore.State, Nothing> {

    @Immutable
    data class State(
        val categories: List<IngredientCategoryCard>,
        val dialogState: SelectIngredientDialogState
    ) {
        @Immutable
        data class IngredientCategoryCard(
            val id: Int,
            val titleResId: Int,
            val iconResId: Int,
            val color: Color,
            val names: List<String>
        )

        sealed interface SelectIngredientDialogState {
            data object Hidden: SelectIngredientDialogState

            @Immutable
            data class Shown(
                val query: String,
                val searchResult: List<Ingredient>
            ): SelectIngredientDialogState
        }
    }

    sealed interface Intent {
        data object Reset : Intent
        data object AddIngredientClick : Intent
        data object HideDialog : Intent
        data object ShowDialog : Intent

        data class RemoveIngredient(val name: String) : Intent
        data class ChangeSearchQuery(val query: String) : Intent
        data class SearchIngredient(val query: String) : Intent
        data class AddIngredient(val name: String) : Intent
    }
}