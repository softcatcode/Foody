package com.softcat.data.implementations

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.softcat.data.mapper.toDbModel
import com.softcat.data.mapper.toEntity
import com.softcat.database.facade.DatabaseFacade
import com.softcat.domain.entities.User
import com.softcat.domain.interfaces.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import timber.log.Timber
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val database: DatabaseFacade,
    private val dataStore: DataStore<Preferences>
): UserRepository {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val loadUserRequest = MutableSharedFlow<Unit>(replay = 1)
    private val userFlow = flow {
        loadUserRequest.emit(Unit)
        loadUserRequest.collect {
            emit(loadLastEnteredUser())
        }
    }

    override suspend fun enter(
        email: String,
        password: String
    ): Result<User> {
        val userModel = database.verifyUser(email, password).getOrElse {
            return Result.failure(it)
        }
        return Result.success(userModel.toEntity())
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<User> {
        val userModel = database.createUser(name, email, password).getOrElse {
            return Result.failure(it)
        }
        return Result.success(userModel.toEntity())
    }

    override suspend fun modifyUser(user: User): Result<Unit> {
        Timber.i("${this::class.simpleName}.modifyUser($user)")
        database.modifyUser(user.toDbModel())
        return Result.success(Unit)
    }

    override suspend fun rememberUser(user: User) {
        Timber.i("${this::class.simpleName}.rememberUser($user)")
        dataStore.edit { preferences ->
            val userKey = stringPreferencesKey(USER_KEY)
            val userJson = Gson().toJson(user)
            preferences[userKey] = userJson
            Timber.i("User $userJson is remembered.")
        }
        loadUserRequest.emit(Unit)
    }

    override suspend fun observeLastEnteredUser(): SharedFlow<User?> {
        Timber.i("${this::class.simpleName}.observeLastEnteredUser()")
        return userFlow.shareIn(
            scope = scope,
            started = SharingStarted.Lazily
        )
    }

    override suspend fun exit() {
        Timber.i("${this::class.simpleName}.forgetUser()")
        database.exit()
        dataStore.edit { preferences ->
            val userKey = stringPreferencesKey(USER_KEY)
            preferences.remove(userKey)
            Timber.i("Last authorized user is forgotten.")
        }
        loadUserRequest.emit(Unit)
    }

    private suspend fun loadLastEnteredUser(): User? {
        Timber.i("${this::class.simpleName}.loadLastEnteredUser()")
        val datastoreKey = stringPreferencesKey(USER_KEY)
        val userJson = dataStore.data.firstOrNull()?.get(datastoreKey)
        val user = userJson?.let {
            Timber.i("SUCCESS: $userJson fetched from internal storage.")
            Gson().fromJson(userJson, User::class.java)
        }
        return user
    }

    companion object {
        private const val USER_KEY = "last_entered_user"
    }
}