package com.softcat.domain.usecases

import com.softcat.domain.entities.Recipe
import com.softcat.domain.interfaces.FavouritesRepository
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject

class FavouritesUseCase @Inject constructor(
    private val repository: FavouritesRepository
) {
    suspend fun add(userId: String, recipeId: Int): Result<Unit> {
        Timber.i("${this::class.simpleName} add($userId, $recipeId) invoked")
        return repository.add(userId, recipeId)
    }

    suspend fun remove(userId: String, recipeId: Int): Result<Unit> {
        Timber.i("${this::class.simpleName} remove($userId, $recipeId) invoked")
        return repository.remove(userId, recipeId)
    }

    suspend fun observe(userId: String): SharedFlow<List<Recipe>> {
        Timber.i("${this::class.simpleName} observe($userId) invoked")
        return repository.observe(userId)
    }

    suspend fun observeFavouriteIds(userId: String): StateFlow<Set<Int>> {
        Timber.i("${this::class.simpleName} getIds($userId) invoked")
        return repository.observeFavouriteIds(userId)
    }

    suspend fun observeIsFavourite(userId: String, recipeId: Int): StateFlow<Boolean> {
        Timber.i("${this::class.simpleName} observeIsFavourite($userId, $recipeId) invoked")
        return repository.observeIsFavourite(userId, recipeId)
    }
}