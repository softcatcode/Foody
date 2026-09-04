package com.softcat.data.implementations

import com.example.recommender.implementations.toEntity
import com.softcat.database.facade.DatabaseFacade
import com.softcat.domain.entities.Ingredient
import com.softcat.domain.interfaces.IngredientRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class IngredientRepositoryImpl @Inject constructor(
    private val database: DatabaseFacade,
): IngredientRepository {

    private val loadIngredientsRequest = MutableSharedFlow<Unit>(replay = 1)
    private val availableIngredientsFlow = flow {
        loadIngredientsRequest.emit(Unit)
        loadIngredientsRequest.collect {
            val ingredients = loadAvailableIngredients()
            emit(ingredients)
        }
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    override suspend fun search(query: String) =
        database.searchIngredient(query, 1000).map { it.toEntity() }

    override fun getAvailableIngredients() = availableIngredientsFlow.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    override suspend fun addAvailableIngredient(ingredientId: Int) {
        database.addAvailableIngredient(ingredientId)
        loadIngredientsRequest.emit(Unit)
    }

    override suspend fun removeAvailableIngredient(ingredientId: Int) {
        database.removeAvailableIngredient(ingredientId)
        loadIngredientsRequest.emit(Unit)
    }

    override suspend fun resetAvailableIngredients() {
        database.resetAvailableIngredients()
        loadIngredientsRequest.emit(Unit)
    }

    override suspend fun getIngredients(limit: Int) =
        database.getIngredients(limit).map { it.toEntity() }

    private suspend fun loadAvailableIngredients(): List<Ingredient> {
        return database.getAvailableIngredients().map { it.toEntity() }
    }
}