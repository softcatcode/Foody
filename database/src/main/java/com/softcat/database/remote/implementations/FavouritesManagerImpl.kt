package com.softcat.database.remote.implementations

import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.database
import com.softcat.database.DatabaseRules
import com.softcat.database.remote.interfaces.FavouritesManager
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class FavouritesManagerImpl @Inject constructor(): FavouritesManager {

    private val favouritesStorage by lazy {
        Firebase.database.getReference(DatabaseRules.FAVOURITES_STORAGE)
    }

    override suspend fun add(
        userId: String,
        recipeId: Int
    ): Result<Unit> {
        return try {
            val favouriteRef = favouritesStorage
                .child(userId)
                .child(recipeId.toString())
            favouriteRef.setValue(true).await()

            Timber.d("Recipe added to favourites: user=$userId, recipe=$recipeId")
            Result.success(Unit)
        } catch (e: DatabaseException) {
            Timber.e(e, "Firebase error adding favourite: user=$userId, recipe=$recipeId")
            Result.failure(e)
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error adding favourite: user=$userId, recipe=$recipeId")
            Result.failure(e)
        }
    }

    override suspend fun remove(
        userId: String,
        recipeId: Int
    ): Result<Unit> {
        return try {
            val favouriteRef = favouritesStorage
                .child(userId)
                .child(recipeId.toString())
            favouriteRef.removeValue().await()
            Timber.d("Recipe removed from favourites: user=$userId, recipe=$recipeId")
            Result.success(Unit)
        } catch (e: DatabaseException) {
            Timber.e(e, "Firebase error removing favourite: user=$userId, recipe=$recipeId")
            Result.failure(e)
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error removing favourite: user=$userId, recipe=$recipeId")
            Result.failure(e)
        }
    }

    override suspend fun get(userId: String): Result<List<Int>> {
        return try {
            val userFavouritesRef = favouritesStorage.child(userId)
            val snapshot = userFavouritesRef.get().await()

            if (!snapshot.exists()) {
                Timber.d("No favourites found for user=$userId")
                return Result.success(emptyList())
            }
            val favourites = snapshot.children.mapNotNull { it.key?.toIntOrNull() }

            Timber.d("Loaded ${favourites.size} favourites for user=$userId")
            Result.success(favourites)
        } catch (e: DatabaseException) {
            Timber.e(e, "Firebase error loading favourites for user=$userId")
            Result.failure(e)
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error loading favourites for user=$userId")
            Result.failure(e)
        }
    }
}