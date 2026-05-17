package com.softcat.domain.interfaces

import com.softcat.domain.entities.Recipe
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow


interface FavouritesRepository {

    suspend fun add(userId: String, recipeId: Int): Result<Unit>

    suspend fun remove(userId: String, recipeId: Int): Result<Unit>

    suspend fun observe(userId: String): SharedFlow<List<Recipe>>

    suspend fun observeFavouriteIds(userId: String): StateFlow<Set<Int>>

    suspend fun observeIsFavourite(userId: String, recipeId: Int): StateFlow<Boolean>
}