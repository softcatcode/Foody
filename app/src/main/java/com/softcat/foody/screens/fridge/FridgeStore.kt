package com.softcat.foody.screens.fridge

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

interface FridgeStore {

    @Immutable
    data class State(
        val categories: List<IngredientCategoryCard>
    ) {
        @Immutable
        data class IngredientCategoryCard(
            val titleResId: Int,
            val iconResId: Int,
            val color: Color,
            val names: List<String>
        )
    }
}