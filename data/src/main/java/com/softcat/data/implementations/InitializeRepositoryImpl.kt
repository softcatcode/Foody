package com.softcat.data.implementations

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.softcat.database.facade.DatabaseFacade
import com.softcat.domain.interfaces.InitializeRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class InitializeRepositoryImpl @Inject constructor(
    private val database: DatabaseFacade,
    private val datastore: DataStore<Preferences>
): InitializeRepository {

    override suspend fun initializeDatabase(requiredCount: Int): Result<Unit> {
        database.initialize(requiredCount).getOrElse {
            return Result.failure(it)
        }
        setInitialized(true)
        return Result.success(Unit)
    }

    override suspend fun isInitialized(): Boolean {
        val key = stringPreferencesKey(IS_INITIALIZED_KEY)
        val status = datastore.data.firstOrNull()?.get(key)
        return status == "true"
    }

    private suspend fun setInitialized(value: Boolean) {
        datastore.edit { preferences ->
            val key = stringPreferencesKey(IS_INITIALIZED_KEY)
            preferences[key] = value.toString()
        }
    }

    companion object {
        private const val IS_INITIALIZED_KEY = "db_initialization"
    }
}