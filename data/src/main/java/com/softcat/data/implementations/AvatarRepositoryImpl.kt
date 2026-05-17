package com.softcat.data.implementations

import android.net.Uri
import com.softcat.domain.interfaces.AvatarRepository
import javax.inject.Inject
import com.softcat.database.facade.DatabaseFacade

class AvatarRepositoryImpl @Inject constructor(
    private val database: DatabaseFacade
): AvatarRepository {

    override suspend fun saveAvatar(
        userId: String,
        uri: Uri
    ): Result<String> {
        val response = database.updateAvatar(userId, uri)
        val result = response.getOrElse {
            return Result.failure(it)
        }
        return Result.success(result)
    }

    override suspend fun getAvatarFromDatabase(userId: String): Result<String> {
        val response = database.getAvatar(userId)
        val avatar = response.getOrElse {
            return Result.failure(it)
        }
        return Result.success(avatar.url)
    }
}