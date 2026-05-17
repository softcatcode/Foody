package com.softcat.database.remote.interfaces

import android.net.Uri
import com.softcat.database.models.AvatarDbModel

interface AvatarsManager {

    suspend fun save(userId: String, uri: Uri): Result<String>

    suspend fun get(userId: String): Result<AvatarDbModel>

    suspend fun delete(userId: String): Result<Unit>
}