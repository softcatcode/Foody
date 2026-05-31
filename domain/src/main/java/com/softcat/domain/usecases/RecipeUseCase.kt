package com.softcat.domain.usecases

import com.softcat.domain.entities.Ingredient
import com.softcat.domain.entities.Recipe
import com.softcat.domain.entities.RecipeTag
import com.softcat.domain.entities.Score
import com.softcat.domain.interfaces.RecipeRepository
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject

class RecipeUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend fun search(
        userId: String?,
        query: String,
    ): List<Recipe> {
        Timber.i("${this::class.simpleName} search($userId, $query) invoked")
        return repository.search(userId, query)
    }

    suspend fun recommend(
        scores: List<Score>,
        ingredients: List<Ingredient>,
        maxAbsentIngredients: Int,
        tags: List<RecipeTag>
    ): List<Recipe> {
        Timber.i("${this::class.simpleName} recommend(List(${scores.size}), $ingredients, $tags) invoked")
        return repository.recommend(scores, ingredients, maxAbsentIngredients, tags)
    }

    suspend fun get(recipeIds: List<Int>): List<Recipe> {
        Timber.i("${this::class.simpleName} get(List(${recipeIds.size})) invoked")
        return repository.get(recipeIds)
    }

    suspend fun setIsCooked(recipeId: Int, value: Boolean): Result<Unit> {
        Timber.i("${this::class.simpleName} setIsCooked() invoked")
        return repository.setIsCooked(recipeId, value)
    }

    suspend fun observeIsCooked(recipeId: Int): StateFlow<Boolean> {
        Timber.i("${this::class.simpleName} observeIsCooked() invoked")
        return repository.observeIsCooked(recipeId)
    }
}