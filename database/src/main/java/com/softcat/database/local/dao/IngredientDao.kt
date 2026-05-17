package com.softcat.database.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.softcat.database.DatabaseRules.INGREDIENTS_TABLE_NAME
import com.softcat.database.models.IngredientDbModel

@Dao
interface IngredientDao {

    @Query("select * from $INGREDIENTS_TABLE_NAME where id = :id")
    suspend fun get(id: Int): IngredientDbModel?

    @Query("select * from $INGREDIENTS_TABLE_NAME where 1 limit :limit")
    suspend fun getSample(limit: Int): List<IngredientDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ingredients: List<IngredientDbModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ingredient: IngredientDbModel)

    @Query("delete from $INGREDIENTS_TABLE_NAME where id = :id")
    suspend fun remove(id: Int)

    @Query("delete from $INGREDIENTS_TABLE_NAME")
    suspend fun clear()

    @Query("""
        select * from $INGREDIENTS_TABLE_NAME
        where lower(name) LIKE '%' || LOWER(:query) || '%'
        limit :limit
    """)
    suspend fun search(query: String, limit: Int): List<IngredientDbModel>
}