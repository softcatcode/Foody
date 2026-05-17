package com.softcat.domain.usecases

import com.softcat.domain.interfaces.InitializeRepository
import timber.log.Timber
import javax.inject.Inject

class InitializeUseCase @Inject constructor(
    private val repository: InitializeRepository
) {
    suspend operator fun invoke(requiredCount: Int) {
        Timber.i("${this::class.simpleName} invoked")
        return repository.initializeDatabase(requiredCount)
    }

    suspend fun isInitialized(): Boolean {
        Timber.i("${this::class.simpleName} isInitialized()")
        return repository.isInitialized()
    }
}