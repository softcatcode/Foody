package com.softcat.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val id: Int,
    val name: String,
    val description: String,
    val steps: List<String>,
    val ingredients: List<Ingredient>,
    val tags: List<RecipeTag>,
    val isCooked: Boolean,
    val minutes: Int,
    val nutrition: NutritionData
)