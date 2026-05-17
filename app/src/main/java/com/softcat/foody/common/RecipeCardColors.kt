package com.softcat.foody.common

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White

data class RecipeCardColors(
    val mainGradient: Brush,
    val ingredientGradient: Brush,
    val ingredientAlpha: Float
) {
    constructor(
        mainGradColor: Color,
        firstIngredientGradColor: Color,
        secondIngredientGradColor: Color,
        ingredientAlpha: Float
    ): this(
        mainGradient = Brush.linearGradient(
            colors = listOf(mainGradColor, White),
            start = Offset(0f, 0f),
            end = Offset(0f, Float.POSITIVE_INFINITY)
        ),
        ingredientGradient = Brush
            .linearGradient(listOf(firstIngredientGradColor, secondIngredientGradColor)),
        ingredientAlpha = ingredientAlpha
    )
}