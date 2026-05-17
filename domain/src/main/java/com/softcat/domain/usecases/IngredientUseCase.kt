package com.softcat.domain.usecases

import com.softcat.domain.entities.Ingredient
import com.softcat.domain.interfaces.IngredientRepository
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
}