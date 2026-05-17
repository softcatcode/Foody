package com.softcat.database.remote.interfaces

import com.softcat.database.models.ScoreDbModel

interface ScoreManager {

    suspend fun add(userId: String, score: ScoreDbModel): Result<Unit>

    suspend fun remove(userId: String, recipeId: Int): Result<Unit>

    suspend fun get(userId: String): Result<List<ScoreDbModel>>

    suspend fun getScoreValue(userId: String, recipeId: Int): Result<Int>
}