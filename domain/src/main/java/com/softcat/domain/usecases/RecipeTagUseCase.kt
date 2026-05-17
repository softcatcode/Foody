package com.softcat.domain.usecases

import com.softcat.domain.entities.RecipeTag
import com.softcat.domain.interfaces.RecipeTagRepository
import timber.log.Timber
import javax.inject.Inject

class RecipeTagUseCase @Inject constructor(
    private val repository: RecipeTagRepository
) {

    suspend fun getTags(limit: Int): List<RecipeTag> {
        Timber.i("${this::class.simpleName} getTags($limit) invoked")
        return repository.getTags(limit)
    }

    suspend fun search(query: String): List<RecipeTag> {
        Timber.i("${this::class.simpleName} search($query) invoked")
        return repository.search(query)
    }
}