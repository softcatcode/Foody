package com.softcat.database.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.softcat.database.DatabaseRules.AVG_SCORES_TABLE_NAME
import com.softcat.database.models.AvgScoreDbModel

@Dao
interface AvgScoreDao {
    @Query("select * from $AVG_SCORES_TABLE_NAME where recipeId in (:ids)")
    suspend fun getAll(ids: List<Int>): List<AvgScoreDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tags: List<AvgScoreDbModel>)

    @Query("select count(*) > 0 from $AVG_SCORES_TABLE_NAME")
    suspend fun isNotEmpty(): Boolean

    @Query("delete from $AVG_SCORES_TABLE_NAME")
    suspend fun clear()
}