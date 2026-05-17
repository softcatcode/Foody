package com.softcat.database.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.softcat.database.DatabaseRules.TAGS_TABLE_NAME
import com.softcat.database.models.TagDbModel

@Dao
interface TagDao {

    @Query("select * from $TAGS_TABLE_NAME where id = :id")
    suspend fun get(id: Int): TagDbModel?

    @Query("select * from $TAGS_TABLE_NAME where 1 limit :limit")
    suspend fun getSample(limit: Int): List<TagDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tags: List<TagDbModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tag: TagDbModel)

    @Query("delete from $TAGS_TABLE_NAME where id = :id")
    suspend fun remove(id: Int)

    @Query("delete from $TAGS_TABLE_NAME")
    suspend fun clear()

    @Query("""
        select * from $TAGS_TABLE_NAME
        where lower(name) LIKE '%' || LOWER(:query) || '%'
        limit :limit
    """)
    suspend fun search(query: String, limit: Int): List<TagDbModel>
}