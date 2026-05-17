package com.softcat.domain.interfaces

import android.net.Uri

interface AvatarRepository {

    suspend fun saveAvatar(userId: String, uri: Uri): Result<String>

    suspend fun getAvatarFromDatabase(userId: String): Result<String>
}