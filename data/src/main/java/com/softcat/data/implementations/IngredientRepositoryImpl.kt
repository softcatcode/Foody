package com.softcat.data.implementations

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.recommender.implementations.toEntity
import com.softcat.database.facade.DatabaseFacade
import com.softcat.domain.entities.Ingredient
import com.softcat.domain.interfaces.IngredientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlinx.serialization.json.Json

class IngredientRepositoryImpl @Inject constructor(
    private val database: DatabaseFacade,
    private val dataStore: DataStore<Preferences>
): IngredientRepository {

    override suspend fun search(query: String) =
        database.searchIngredient(query, 1000).map { it.toEntity() }

    override suspend fun getAvailableIngredients(): Flow<List<Ingredient>> {
        val key = stringPreferencesKey(AVAILABLE_INGREDIENTS_KEY)
        if (!dataStore.data.first().contains(key))
            initializeDefaultAvailableIngredients()
        return dataStore.data.map { preferences ->
            val stringIds = preferences[key].orEmpty()
            val idList = Json.decodeFromString<List<Int>>(stringIds)
            val ingredientModels = database.getIngredients(idList)
            ingredientModels.map { it.toEntity() }
        }
    }

    override suspend fun setAvailableIngredients(ingredientIds: List<Int>) {
        val jsonString = Json.encodeToString(ingredientIds)
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