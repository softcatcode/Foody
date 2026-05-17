package com.softcat.domain.interfaces

import com.softcat.domain.entities.Ingredient
import com.softcat.domain.entities.Recipe
import com.softcat.domain.entities.RecipeTag
import com.softcat.domain.entities.Score
import kotlinx.coroutines.flow.StateFlow

interface RecipeRepository {
    suspend fun search(userId: String?, query: String): List<Recipe>

    suspend fun recommend(
        scores: List<Score>,
        ingredients: List<Ingredient>,
        tags: List<RecipeTag>
    ): List<Recipe>

    suspend fun get(recipeIds: List<Int>): List<Recipe>

    suspend fun setIsCooked(recipeId: Int, value: Boolean): Result<Unit>

    suspend fun observeIsCooked(recipeId: Int): StateFlow<Boolean>
}