package com.softcat.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.softcat.database.DatabaseRules

@Entity(tableName = DatabaseRules.RECIPE_VECTORS_TABLE_NAME)
data class RecipeVectorDbModel(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "vector")
    val vector: List<Float>
)