package com.softcat.database.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.softcat.database.DatabaseRules.SCORES_TABLE_NAME
import com.softcat.database.models.ScoreDbModel

@Dao
interface ScoreDao {

    @Query("select * from $SCORES_TABLE_NAME where recipeId = :recipeId")
    suspend fun get(recipeId: Int): ScoreDbModel?

    @Query("select * from $SCORES_TABLE_NAME")
    suspend fun getAll(): List<ScoreDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(scores: List<ScoreDbModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(score: ScoreDbModel)

    @Query("delete from $SCORES_TABLE_NAME where recipeId = :recipeId")
    suspend fun remove(recipeId: Int)

    @Query("delete from $SCORES_TABLE_NAME")
    suspend fun clear()
}