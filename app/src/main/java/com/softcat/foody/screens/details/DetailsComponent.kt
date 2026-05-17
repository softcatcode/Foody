package com.softcat.foody.screens.details

import kotlinx.coroutines.flow.StateFlow

interface DetailsComponent {

    val model: StateFlow<DetailsStore.State>

    fun addToFavourites()
    fun removeFromFavourites()

    fun updateScore(newValue: Int)

    fun deleteScore()

    fun changeIsCooked(newValue: Boolean)

    fun nextStep()
    fun previousStep()

    fun back()
}