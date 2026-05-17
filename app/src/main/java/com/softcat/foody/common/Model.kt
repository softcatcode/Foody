package com.softcat.foody.common

data class RecipeModel(
    val id: Int,
    val favouriteButtonVisible: Boolean,
    val isFavourite: Boolean,
    val ingredients: List<String>,
    val name: String,
    val description: String,
    val score: String,
    val scoreVisible: Boolean
)