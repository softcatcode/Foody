package com.softcat.data.implementations

import com.softcat.database.facade.DatabaseFacade
import com.softcat.domain.entities.RecipeTag
import com.softcat.domain.interfaces.RecipeTagRepository
import javax.inject.Inject

class RecipeTagRepositoryImpl @Inject constructor(
    private val database: DatabaseFacade,
): RecipeTagRepository {

    override suspend fun search(query: String) =
        database.searchTag(query, 1000).map { RecipeTag(it.name) }

    override suspend fun getTags(limit: Int) =
        database.getTags(limit).map { RecipeTag(it.name) }
}