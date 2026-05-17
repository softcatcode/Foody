package com.softcat.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.softcat.database.DatabaseRules

@Entity(tableName = DatabaseRules.INGREDIENTS_TABLE_NAME)
data class IngredientDbModel(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String,
)