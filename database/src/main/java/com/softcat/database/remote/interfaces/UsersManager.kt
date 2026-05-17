package com.softcat.database.remote.interfaces

import com.softcat.database.models.UserDbModel

interface UsersManager {
    suspend fun createUser(name: String, email: String, password: String): Result<UserDbModel>

    suspend fun enter(email: String, password: String): Result<UserDbModel>

    suspend fun modify(user: UserDbModel): Result<Unit>

    suspend fun exit()
}