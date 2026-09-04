package com.softcat.data.implementations

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.recommender.implementations.toEntity
import com.softcat.database.facade.DatabaseFacade
import com.softcat.domain.entities.Ingredient
import com.softcat.domain.interfaces.IngredientRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlinx.serialization.json.Json

class IngredientRepositoryImpl @Inject constructor(
    private val database: DatabaseFacade,
    private val dataStore: DataStore<Preferences>
): IngredientRepository {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val availableIngredientIds: StateFlow<List<Int>> = dataStore.data.map { preferences ->
        val key = stringPreferencesKey(AVAILABLE_INGREDIENTS_KEY)
        val idsStr = preferences[key].orEmpty()
        Json.decodeFromString<List<Int>>(idsStr)
    }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    override suspend fun search(query: String) =
        database.searchIngredient(query, 1000).map { it.toEntity() }

    override suspend fun getAvailableIngredients(): Flow<List<Ingredient>> {
        if (availableIngredientIds.value.isEmpty())
            initializeDefaultAvailableIngredients()

        return availableIngredientIds.map { idList ->
            val ingredientModels = database.getIngredients(idList)
            ingredientModels.map { it.toEntity() }
        }
    }

    override suspend fun addAvailableIngredient(ingredientId: Int) {
        val idList = availableIngredientIds.value.toMutableList()
        if (ingredientId !in idList)
            idList.add(ingredientId)
        val jsonString = Json.encodeToString(idList)
        val key = stringPreferencesKey(AVAILABLE_INGREDIENTS_KEY)
        dataStore.edit { preferences ->
            preferences[key] = jsonString
        }
    }

    override suspend fun removeAvailableIngredient(ingredientId: Int) {
        val idList = availableIngredientIds.value.toMutableList()
        idList.remove(ingredientId)
        val jsonString = Json.encodeToString(idList)
        val key = stringPreferencesKey(AVAILABLE_INGREDIENTS_KEY)
        dataStore.edit { preferences ->
            preferences[key] = jsonString
        }
    }

    override suspend fun resetAvailableIngredients() {
        initializeDefaultAvailableIngredients()
    }

    override suspend fun getIngredients(limit: Int) =
        database.getIngredients(limit).map { it.toEntity() }

    private suspend fun initializeDefaultAvailableIngredients() {
        val defaultAvailableIngredientIds = database.getDefaultAvailableIngredients().map { it.id }
        val idListString = Json.encodeToString(defaultAvailableIngredientIds)
        val key = stringPreferencesKey(AVAILABLE_INGREDIENTS_KEY)
        dataStore.edit { preferences ->
            preferences[key] = idListString
        }
    }

    companion object {
        private const val AVAILABLE_INGREDIENTS_KEY = "available_ingredients"
    }
}