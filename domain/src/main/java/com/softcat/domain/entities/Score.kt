package com.softcat.domain.entities

import java.util.Calendar

data class Score(
    val recipeId: Int,
    val value: Int,
    val date: Calendar
)