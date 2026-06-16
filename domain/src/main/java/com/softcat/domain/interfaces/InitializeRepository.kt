package com.softcat.domain.interfaces

interface InitializeRepository {
    suspend fun initializeDatabase(requiredCount: Int): Result<Unit>

    suspend fun isInitialized(): Boolean
}