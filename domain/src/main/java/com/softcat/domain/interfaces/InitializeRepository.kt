package com.softcat.domain.interfaces

interface InitializeRepository {
    suspend fun initializeDatabase(requiredCount: Int)

    suspend fun isInitialized(): Boolean
}