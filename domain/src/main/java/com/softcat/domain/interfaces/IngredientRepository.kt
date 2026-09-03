package com.softcat.domain.interfaces

import com.softcat.domain.entities.Ingredient
import kotlinx.coroutines.flow.Flow

interface IngredientRepository {
    suspend fun getIngredients(limit: Int): List<Ingredient>

    suspend fun search(query: String): List<Ingredient>

    suspend fun getAvailableIngredients(): Flow<List<Ingredient>>

    suspend fun setAvailableIngredients(ingredientIds: List<Int>)

    suspend fun resetAvailableIngredients()
}