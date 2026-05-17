package com.softcat.foody.screens.scores

data class RecipeScoreModel(
    val id: Int,
    val recipeId: Int,
    val score: Int,
    val name: String,
    val description: String,
    val isFavouriteVisible: Boolean,
    val isFavourite: Boolean,
    val date: String
)