package com.softcat.domain.interfaces

import android.content.Context

interface InitializeRepository {
    suspend fun initializeDatabase(requiredCount: Int)

    suspend fun isInitialized(): Boolean
}