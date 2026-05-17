package com.softcat.database.remote.interfaces

interface FavouritesManager {

    suspend fun add(userId: String, recipeId: Int): Result<Unit>

    suspend fun remove(userId: String, recipeId: Int): Result<Unit>

    suspend fun get(userId: String): Result<List<Int>>
}