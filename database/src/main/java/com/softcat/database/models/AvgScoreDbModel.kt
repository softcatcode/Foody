package com.softcat.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.softcat.database.DatabaseRules.AVG_SCORES_TABLE_NAME

@Entity(tableName = AVG_SCORES_TABLE_NAME)
data class AvgScoreDbModel(
    @PrimaryKey
    @ColumnInfo(name = "recipeId")
    val recipeId: Int,

    @ColumnInfo(name = "value")
    val value: Float
)
