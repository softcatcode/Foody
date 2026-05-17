package com.softcat.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class NutritionData(
    val calories: Float,
    val fat: Float,
    val sugar: Float,
    val sodium: Float,
    val protein: Float,
    val saturatedFat: Float,
    val carbohydrates: Float,
)