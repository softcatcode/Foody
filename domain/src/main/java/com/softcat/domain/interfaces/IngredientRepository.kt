package com.softcat.domain.interfaces

import com.softcat.domain.entities.Ingredient

interface IngredientRepository {
    suspend fun getIngredients(limit: Int): List<Ingredient>

    suspend fun search(query: String): List<Ingredient>
}