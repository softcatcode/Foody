package com.softcat.database.remote.implementations

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.softcat.database.DatabaseRules
import com.softcat.database.exceptions.DataCorruptedException
import com.softcat.database.exceptions.UserNotFoundException
import com.softcat.database.models.UserDbModel
import com.softcat.database.remote.interfaces.UsersManager
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject

class UsersManagerImpl @Inject constructor(): UsersManager {

    private val usersStorage by lazy {
        Firebase.database.getReference(DatabaseRules.USERS_STORAGE)
    }

    private val auth by lazy {
        Firebase.auth
    }

    override suspend fun createUser(
        name: String,
        email: String,
        password: String
    ): Result<UserDbModel> {
        return try {
            val registerJob = auth.createUserWithEmailAndPassword(email, password)
            registerJob.await()
            registerJob.exception?.let { return Result.failure(it) }
            if (registerJob.result.additionalUserInfo?.isNewUser == false) {
                return readUser(email)
            }
            val reference = usersStorage.push()
            val user = UserDbModel(
                id = reference.key.toString(),
                email = email,
                name = name,
                registerDate = Calendar.getInstance().timeInMillis / 1000L
            )
            val job = reference.setValue(user)
            job.await()
            job.exception?.let {
                auth.signOut()
                return Result.failure(it)
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun enter(
        email: String,
        password: String
    ): Result<UserDbModel> {
        try {
            val verifyJob = auth.signInWithEmailAndPassword(email, password)
            verifyJob.await()
            verifyJob.exception?.let { return Result.failure(it) }
            return readUser(email)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private suspend fun readUser(email: String): Result<UserDbModel> {
        val query = usersStorage.orderByChild("email").equalTo(email)
        val result = query.get().await()
        if (!result.exists()) {
            return Result.failure(UserNotFoundException(email))
        }

        val userSnapshot = result.children.first()
        val user = userSnapshot.getValue<UserDbModel>()
            ?: return Result.failure(
                DataCorruptedException("user data is invalid")
            )
        return Result.success(user)
    }

    override suspend fun modify(user: UserDbModel): Result<Unit> {
        return try {
            val query = usersStorage.orderByChild("id").equalTo(user.id)
            val result = query.get().await()
            if (result.exists())
                query.ref.setValue(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun exit() {
        auth.signOut()
    }
}