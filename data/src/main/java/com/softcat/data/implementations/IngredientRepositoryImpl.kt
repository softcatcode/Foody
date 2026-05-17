package com.softcat.data.implementations

import com.softcat.data.mapper.toEntity
import com.softcat.database.facade.DatabaseFacade
import com.softcat.domain.interfaces.IngredientRepository
import javax.inject.Inject

class IngredientRepositoryImpl @Inject constructor(
    private val database: DatabaseFacade,
): IngredientRepository {

    override suspend fun search(query: String) =
        database.searchIngredient(query, 1000).map { it.toEntity() }

    override suspend fun getIngredients(limit: Int) =
        database.getIngredients(limit).map { it.toEntity() }
}