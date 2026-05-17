package com.softcat.domain.usecases

import android.net.Uri
import com.softcat.domain.interfaces.AvatarRepository
import timber.log.Timber
import javax.inject.Inject

class UserAvatarUseCase @Inject constructor(
    private val repository: AvatarRepository
) {
    suspend fun save(userId: String, uri: Uri): Result<String> {
        Timber.i("${this::class.simpleName} saveAvatar($uri) invoked")
        return repository.saveAvatar(userId, uri)
    }

    suspend fun get(userId: String): Result<String> {
        Timber.i("${this::class.simpleName} get($userId) invoked")
        return repository.getAvatarFromDatabase(userId)
    }
}