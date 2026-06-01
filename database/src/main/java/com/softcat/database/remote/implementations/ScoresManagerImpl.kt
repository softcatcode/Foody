package com.softcat.database.remote.implementations

import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.softcat.database.DatabaseRules
import com.softcat.database.exceptions.ScoreIsAbsentException
import com.softcat.database.exceptions.ScoresNodeIsAbsentException
import com.softcat.database.local.dao.ScoreDao
import com.softcat.database.models.ScoreDbModel
import com.softcat.database.remote.interfaces.ScoreManager
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class ScoresManagerImpl @Inject constructor(
    private val scoreDao: ScoreDao
): ScoreManager {

    private val scoresStorage by lazy {
        Firebase.database.getReference(DatabaseRules.SCORES_STORAGE)
    }

    override suspend fun add(
        userId: String,
        score: ScoreDbModel
    ): Result<Unit> {
        return try {
            val scoreRef = scoresStorage
                .child(userId)
                .child(score.recipeId.toString())
            scoreRef.setValue(score).await()

            Timber.d("Score saved: user=$userId, recipe=${score.recipeId}, value=${score.value}")
            scoreDao.insert(score) // Кеширование в локальную БД.

            Result.success(Unit)
        } catch (e: DatabaseException) {
            Timber.e(e, "Firebase error saving score for user=$userId, recipe=${score.recipeId}")
            Result.failure(e)
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error saving score for user=$userId, recipe=${score.recipeId}")
            Result.failure(e)
        }
    }

    override suspend fun remove(
        userId: String,
        recipeId: Int
    ): Result<Unit> {
        return try {
            val scoreRef = scoresStorage
                .child(userId)
                .child(recipeId.toString())

            val exists = scoreRef.get().await().exists()
            if (!exists) {
                Timber.w("Attempt to remove non-existent score: user=$userId, recipe=$recipeId")
                return Result.success(Unit)
            }
            scoreRef.removeValue().await()

            Timber.d("Score removed: user=$userId, recipe=$recipeId")
            scoreDao.remove(recipeId) // Обновление копии оценок.

            Result.success(Unit)
        } catch (e: DatabaseException) {
            Timber.e(e, "Firebase error removing score for user=$userId, recipe=$recipeId")
            Result.failure(e)
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error removing score for user=$userId, recipe=$recipeId")
            Result.failure(e)
        }
    }

    override suspend fun get(userId: String): Result<List<ScoreDbModel>> {
        val requestResult = loadScoresRemote(userId)
        if (requestResult.isSuccess)
            return requestResult

        return try {
            val localScores = scoreDao.getAll()
            Result.success(localScores)
        } catch (_: Exception) {
            requestResult
        }
    }

    override suspend fun getScoreValue(userId: String, recipeId: Int): Result<Int> {
        val requestResult = loadScoreValueRemote(userId, recipeId)
        if (requestResult.isSuccess)
            return requestResult

        return try {
            val localScore = scoreDao.get(recipeId)!!
            Result.success(localScore.value)
        } catch (_: Exception) {
            requestResult
        }
    }

    override suspend fun updateScoreCache(userId: String?): Result<Unit> {
        if (userId == null) {
            scoreDao.clear()
            return Result.success(Unit)
        } else {
            val scores = get(userId).getOrElse {
                return Result.failure(it)
            }
            return try {
                scoreDao.clear()
                scoreDao.insertAll(scores)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private suspend fun loadScoresRemote(userId: String): Result<List<ScoreDbModel>> {
        return try {
            val userScoresRef = scoresStorage.child(userId)
            val snapshot = userScoresRef.get().await()

            if (!snapshot.exists()) {
                Timber.d("No scores found for user=$userId")
                return Result.failure(ScoresNodeIsAbsentException(userId))
            }
            val scores = snapshot.children
                .mapNotNull { it.getValue<ScoreDbModel>() }
                .sortedByDescending { it.date }

            Timber.d("Loaded ${scores.size} scores for user=$userId")
            Result.success(scores)
        } catch (e: DatabaseException) {
            Timber.e(e, "Firebase error loading scores for user=$userId")
            Result.failure(e)
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error loading scores for user=$userId")
            Result.failure(e)
        }
    }

    suspend fun loadScoreValueRemote(userId: String, recipeId: Int): Result<Int> {
        return try {
            val userScoresRef = scoresStorage
                .child(userId)
                .child(recipeId.toString())
            val snapshot = userScoresRef.get().await()

            if (!snapshot.exists()) {
                Timber.d("No scores found for user=$userId and recipe=$recipeId")
                return Result.failure(ScoreIsAbsentException(userId, recipeId))
            }
            val score = snapshot.getValue<ScoreDbModel>()?.value ?: return Result.failure(
                Exception(
                    "Score value for user=$userId and recipe=$recipeId is null"
                )
            )
            Timber.d("Loaded score $score for user=$userId and recipe $recipeId")
            Result.success(score)
        } catch (e: DatabaseException) {
            Timber.e(e, "Firebase error loading scores for user=$userId")
            Result.failure(e)
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error loading scores for user=$userId")
            Result.failure(e)
        }
    }
}