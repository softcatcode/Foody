package com.softcat.domain.entities

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class RecipeTag(
    val name: String
)