package com.softcat.foody.screens.recomend

data class RecipeRecommendationModel(
    val id: Int,
    val name: String,
    val description: String,
    val isFavourite: Boolean,
    val isFavouriteVisible: Boolean
)