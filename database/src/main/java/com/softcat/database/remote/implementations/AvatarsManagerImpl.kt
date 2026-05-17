package com.softcat.database.remote.implementations

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.softcat.database.DatabaseRules
import com.softcat.database.exceptions.AvatarIsAbsentException
import com.softcat.database.models.AvatarDbModel
import com.softcat.database.remote.interfaces.AvatarsManager
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.util.UUID
import javax.inject.Inject

class AvatarsManagerImpl @Inject constructor(
    private val imageLoader: S3ImageLoader
): AvatarsManager {

    private val avatarsStorage by lazy {
        Firebase.database.getReference(DatabaseRules.AVATARS_STORAGE)
    }

    override suspend fun save(
        userId: String,
        uri: Uri
    ): Result<String> {
        return try {
            Result.success(writeAvatarToDatabase(userId, uri))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun get(userId: String): Result<AvatarDbModel> {
        return try {
            val result = readAvatarFromDatabase(userId)
            Result.success(result!!)
        } catch (_: NullPointerException) {
            Result.failure(AvatarIsAbsentException())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun delete(userId: String): Result<Unit> {
        return try {
            removeAvatarFromDatabase(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun writeAvatarToDatabase(userId: String, uri: Uri): String {
        return withTimeout(DatabaseRules.TIMEOUT) {
            val avatarId = UUID.randomUUID().toString()
            val avatarUrl = imageLoader.uploadImageToS3(uri, avatarId)
            val avatar = AvatarDbModel(
                id = avatarId,
                userId = userId,
                url = avatarUrl
            )
            avatarsStorage
                .child(avatarId)
                .setValue(avatar)
                .await()
            avatarUrl
        }
    }

    private suspend fun readAvatarFromDatabase(userId: String): AvatarDbModel? {
        return withTimeout(DatabaseRules.TIMEOUT) {
            val job = avatarsStorage
                .orderByChild("userId")
                .equalTo(userId)
                .get()
            val data = job.await()
            val elem = data.children.firstOrNull()
            elem?.getValue<AvatarDbModel>()
        }
    }

    private suspend fun removeAvatarFromDatabase(userId: String) {
        withTimeout(DatabaseRules.TIMEOUT) {
            val snapshot = avatarsStorage
                .orderByChild("userId")
                .equalTo(userId)
                .get()
                .await()
            val avatar = snapshot.children.firstOrNull()?.getValue<AvatarDbModel>()
            val avatarId = avatar?.id ?: return@withTimeout
            imageLoader.deleteImageFromS3(avatarId)
            avatarsStorage
                .child(avatarId)
                .removeValue()
        }
    }
}