package com.softcat.foody.screens.details

import kotlinx.coroutines.flow.StateFlow

interface DetailsComponent {

    val model: StateFlow<DetailsStore.State>

    fun changeFavouriteStatus()

    fun updateScore(newValue: Int)

    fun deleteScore()

    fun changeIsCooked()

    fun nextStep()
    fun previousStep()

    fun back()
}