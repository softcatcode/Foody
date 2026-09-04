package com.softcat.domain.usecases

import com.softcat.domain.entities.Ingredient
import com.softcat.domain.interfaces.IngredientRepository
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class IngredientUseCase @Inject constructor(
    private val repository: IngredientRepository
) {

    suspend fun getIngredients(limit: Int): List<Ingredient> {
        Timber.i("${this::class.simpleName} getIngredients($limit) invoked")
        return repository.getIngredients(limit)
    }

    suspend fun search(query: String): List<Ingredient> {
        Timber.i("${this::class.simpleName} search($query) invoked")
        return repository.search(query)
    }

    suspend fun getAvailableIngredients(): Flow<List<Ingredient>> {
        Timber.i("${this::class.simpleName} getAvailableIngredients() invoked")
        return repository.getAvailableIngredients()
    }

    suspend fun addAvailableIngredient(ingredientId: Int) {
        Timber.i("${this::class.simpleName} addAvailableIngredient($ingredientId) invoked")
        repository.addAvailableIngredient(ingredientId)
    }

    suspend fun removeAvailableIngredient(ingredientId: Int) {
        Timber.i("${this::class.simpleName} removeAvailableIngredient($ingredientId) invoked")
        repository.removeAvailableIngredient(ingredientId)
    }

    suspend fun resetAvailableIngredients() {
        Timber.i("${this::class.simpleName} resetAvailableIngredients() invoked")
        repository.resetAvailableIngredients()
    }
}