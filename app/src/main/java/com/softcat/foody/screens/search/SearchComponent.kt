package com.softcat.foody.screens.search

import com.softcat.domain.entities.FilterParams
import kotlinx.coroutines.flow.StateFlow

interface SearchComponent {

    val model: StateFlow<SearchStore.State>

    fun changeScore(newValue: Int)

    fun changeCookingTime(newValue: ClosedFloatingPointRange<Float>)

    fun changeCalories(newValue: ClosedFloatingPointRange<Float>)

    fun changeIsCookedStatus(newValue: FilterParams.TripleChoice)

    fun tagClicked(name: String)

    fun ingredientClicked(name: String)

    fun search(query: String)

    fun changeFavouriteStatus(recipeId: Int)

    fun openRecipeDetails(recipeId: Int)

    fun changeQuery(newValue: String)

    fun expandFiltersSheet()

    fun hideFiltersSheet()

    fun resetFilters()
}