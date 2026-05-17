package com.softcat.domain.interfaces

import com.softcat.domain.entities.RecipeTag

interface RecipeTagRepository {
    suspend fun getTags(limit: Int): List<RecipeTag>

    suspend fun search(query: String): List<RecipeTag>
}