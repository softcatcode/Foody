package com.softcat.domain.interfaces

import com.softcat.domain.entities.User
import kotlinx.coroutines.flow.SharedFlow

interface UserRepository {

    suspend fun enter(email: String, password: String): Result<User>

    suspend fun register(name: String, email: String, password: String): Result<User>

    suspend fun modifyUser(user: User): Result<Unit>

    suspend fun rememberUser(user: User)

    suspend fun observeLastEnteredUser(): SharedFlow<User?>

    suspend fun exit()
}