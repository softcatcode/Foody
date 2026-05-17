package com.softcat.data.implementations

import com.softcat.data.mapper.RecipeMapper
import com.softcat.database.facade.DatabaseFacade
import com.softcat.domain.entities.Ingredient
import com.softcat.domain.entities.Recipe
import com.softcat.domain.entities.RecipeTag
import com.softcat.domain.entities.Score
import com.softcat.domain.interfaces.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val database: DatabaseFacade,
    private val recipeMapper: RecipeMapper
): RecipeRepository {

    private val isCookedFlow = MutableStateFlow(false)
    private var selectedRecipe: Int? = null

    override suspend fun search(
        userId: String?,
        query: String,
    ): List<Recipe> {
        return recipeMapper.toEntities(database.searchRecipe(query, limit = 150))
    }

    override suspend fun recommend(
        scores: List<Score>,
        ingredients: List<Ingredient>,
        tags: List<RecipeTag>
    ): List<Recipe> {
        return emptyList()
    }

    override suspend fun get(recipeIds: List<Int>): List<Recipe> {
        return recipeMapper.toEntities(database.getRecipes(recipeIds))
    }

    override suspend fun setIsCooked(recipeId: Int, value: Boolean): Result<Unit> {
        val result = database.setRecipeIsCooked(recipeId, value)
        // Если наблюдаем состояние isCooked данного рецепта, то уведомляем об обновлении.
        selectedRecipe?.let {
            isCookedFlow.value = database.isRecipeCooked(it)
        }
        return result
    }

    override suspend fun observeIsCooked(recipeId: Int): StateFlow<Boolean> {
        selectedRecipe = recipeId
        isCookedFlow.value = database.isRecipeCooked(recipeId)
        return isCookedFlow
    }
}