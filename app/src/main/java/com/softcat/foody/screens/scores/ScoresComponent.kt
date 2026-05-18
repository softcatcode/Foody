package com.softcat.foody.screens.scores

import kotlinx.coroutines.flow.StateFlow

interface ScoresComponent {

    val model: StateFlow<ScoresStore.State>

    fun back()

    fun remove(recipeId: Int)

    fun changeScoreValue(recipeId: Int, newValue: Int)

    fun changeFavouriteStatus(recipeId: Int)

    fun changeIsCookedFilter(newValue: Boolean)
}