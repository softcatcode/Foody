package com.softcat.data.implementations

import com.example.recommender.implementations.RecipeMapper
import com.softcat.database.facade.DatabaseFacade
import com.softcat.domain.entities.Recipe
import com.softcat.domain.interfaces.FavouritesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class FavouritesRepositoryImpl @Inject constructor(
    private val database: DatabaseFacade,
    private val recipeMapper: RecipeMapper
): FavouritesRepository {

    private val scope = CoroutineScope(Dispatchers.IO)

    private var selectedUserId: String? = null

    private val loadFavouritesRequest = MutableSharedFlow<Unit>(replay = 1)
    private val favouritesFlow = flow {
        loadFavouritesRequest.emit(Unit)
        loadFavouritesRequest.collect {
            val favourites = loadFavouriteRecipes().getOrDefault(emptyList())
            emit(favourites)
        }
    }

    override suspend fun add(
        userId: String,
        recipeId: Int
    ): Result<Unit> {
        database.addToFavourites(userId, recipeId)
        loadFavouritesRequest.emit(Unit)
        return Result.success(Unit)
    }

    override suspend fun remove(
        userId: String,
        recipeId: Int
    ): Result<Unit> {
        database.removeFromFavourites(userId, recipeId)
        loadFavouritesRequest.emit(Unit)
        return Result.success(Unit)
    }

    override suspend fun observe(userId: String): SharedFlow<List<Recipe>> {
        selectedUserId = userId
        return favouritesFlow.shareIn(
            scope = scope,
            started = SharingStarted.Lazily,
        )
    }

    override suspend fun observeFavouriteIds(userId: String): StateFlow<Set<Int>> {
        selectedUserId = userId
        return favouritesFlow.transform { recipes ->
            val ids = recipes.map { it.id }
            emit(ids.toSet())
        }.stateIn(
            scope = scope,
            started = SharingStarted.Lazily,
            initialValue = emptySet(),
        )
    }

    override suspend fun observeIsFavourite(
        userId: String,
        recipeId: Int
    ): StateFlow<Boolean> {
        selectedUserId = userId
        return favouritesFlow.transform { recipes ->
            emit(recipes.find { it.id == recipeId } != null)
        }.stateIn(
            scope = scope,
            started = SharingStarted.Lazily,
            initialValue = false,
        )
    }

    private suspend fun loadFavouriteRecipes(): Result<List<Recipe>> {
        val userId = selectedUserId ?: return Result.failure(Exception("User is no authorized."))
        val recipeModels = database.getFavourites(userId)
            .getOrElse { return Result.failure(it) }
        return try {
            val recipes = recipeMapper.toEntities(recipeModels)
            Result.success(recipes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}