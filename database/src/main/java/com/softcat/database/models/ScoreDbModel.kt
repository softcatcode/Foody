package com.softcat.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.softcat.database.DatabaseRules

@Entity(tableName = DatabaseRules.SCORES_TABLE_NAME)
data class ScoreDbModel(
    @PrimaryKey
    val recipeId: Int = 0,
    
    val value: Int = 0,
    val date: Long = 0L
)