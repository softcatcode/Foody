package com.softcat.foody.common

import androidx.compose.ui.graphics.Color

object RecipeCardGradients {
    private val gradients = listOf(
        RecipeCardColors(
            mainGradColor = Color(0xFFE87911),
            firstIngredientGradColor = Color(0xFFFFB366),
            secondIngredientGradColor = Color(0xFFE87911),
            ingredientAlpha = 0.6f
        ),
        RecipeCardColors(
            mainGradColor = Color(0xFF8C23BE),
            firstIngredientGradColor = Color(0xFF7F00FF),
            secondIngredientGradColor = Color(0xFFA87DE8),
            ingredientAlpha = 0.4f
        ),
        RecipeCardColors(
            mainGradColor = Color(0xFF1C710A),
            firstIngredientGradColor = Color(0xFF99D269),
            secondIngredientGradColor = Color(0xFF1C710A),
            ingredientAlpha = 0.7f
        ),
        RecipeCardColors(
            mainGradColor = Color(0xFF142DC8),
            firstIngredientGradColor = Color(0xFF66B2FF),
            secondIngredientGradColor = Color(0xFF142DC8),
            ingredientAlpha = 0.7f
        ),
        RecipeCardColors(
            mainGradColor = Color(0xFF138C9A),
            firstIngredientGradColor = Color(0xFF8BC3FB),
            secondIngredientGradColor = Color(0xFF138C9A),
            ingredientAlpha = 0.9f
        ),
        RecipeCardColors(
            mainGradColor = Color(0xFFAF2489),
            firstIngredientGradColor = Color(0xFFD89FFF),
            secondIngredientGradColor = Color(0xFFAF2489),
            ingredientAlpha = 0.7f
        )
    )

    fun getColors(index: Int): RecipeCardColors {
        val colorIndex = (index + 1) % gradients.size
        return gradients[colorIndex]
    }
}