package com.softcat.domain.usecases

import com.softcat.domain.entities.User
import com.softcat.domain.interfaces.UserRepository
import kotlinx.coroutines.flow.SharedFlow
import timber.log.Timber
import javax.inject.Inject

class UserUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend fun enter(email: String, password: String): Result<User> {
        Timber.i("${this::class.simpleName} enter($email, $password) invoked")
        val user = repository.enter(email, password).getOrElse {
            Timber.i("${this::class.simpleName} logging in failed: ${it.message}")
            return Result.failure(it)
        }
        repository.rememberUser(user)
        return Result.success(user)
    }

    suspend fun exit() {
        Timber.i("${this::class.simpleName} exit() invoked")
        repository.exit()
    }

    suspend fun register(
        name: String,
        email: String,
        password: Pair<String, String>
    ): Result<User> {
        Timber.i("${this::class.simpleName} register($name, $email, $password) invoked")
        if (password.first != password.second)
            return Result.failure(Exception("Passwords do not match."))
        val user = repository.register(name, email, password.first).getOrElse {
            Timber.i("${this::class.simpleName} signing in failed: ${it.message}")
            return Result.failure(it)
        }
        repository.rememberUser(user)
        return Result.success(user)
    }

    suspend fun modify(user: User): Result<Unit> {
        Timber.i("${this::class.simpleName} modify($user) invoked")
        val result = repository.modifyUser(user)
        result.onSuccess { repository.rememberUser(user) }
        return Result.success(Unit)
    }

    suspend fun observeLastEnteredUser(): SharedFlow<User?> {
        Timber.i("${this::class.simpleName} observeLastEnteredUser() invoked")
        return repository.observeLastEnteredUser()
    }
}