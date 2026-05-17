package com.softcat.database.remote.interfaces

interface InitializeManager {
    suspend fun initializeRecipes(requiredCount: Int): Result<Unit>

    suspend fun initializeAvgScores(): Result<Unit>
}