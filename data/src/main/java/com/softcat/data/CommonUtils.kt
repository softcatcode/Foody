package com.softcat.data

import com.softcat.domain.entities.FilterParams
import com.softcat.domain.entities.Recipe

fun List<Recipe>.filter(params: FilterParams, scores: Map<Int, Float>?): List<Recipe> {
    return filter { recipe ->
        if (params.isCooked == FilterParams.TripleChoice.No && recipe.isCooked)
            return@filter false
        if (params.isCooked == FilterParams.TripleChoice.Yes && !recipe.isCooked)
            return@filter false
        params.tags.forEach { tag ->
            if (recipe.tags.find { it.name == tag } == null)
                return@filter false
        }
        params.ingredients.forEach { ingredient ->
            if (recipe.ingredients.find { it.name == ingredient } == null)
                return@filter false
        }
        if (recipe.nutrition.calories !in params.calories)
            return@filter false
        if (recipe.minutes.toFloat() !in params.duration)
            return@filter false
        val userScore = scores?.get(recipe.id)
        if (userScore != null && userScore < params.minScore)
            return@filter false
        true
    }
}