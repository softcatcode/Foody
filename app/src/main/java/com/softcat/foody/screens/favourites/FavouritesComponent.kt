package com.softcat.foody.screens.favourites

import com.softcat.domain.entities.FilterParams
import kotlinx.coroutines.flow.StateFlow

interface FavouritesComponent {

    val model: StateFlow<FavouritesStore.State>

    fun removeFromFavourites(recipeId: Int)

    fun changeScore(newValue: Int)

    fun changeCookingTime(newValue: ClosedFloatingPointRange<Float>)

    fun changeCalories(newValue: ClosedFloatingPointRange<Float>)

    fun changeIsCookedStatus(newValue: FilterParams.TripleChoice)

    fun tagClicked(name: String)

    fun ingredientClicked(name: String)

    fun openRecommendationScreen()

    fun openSearchScreen()

    fun openRecipeDetailsScreen(recipeId: Int)

    fun expandFiltersSheet()

    fun hideFiltersSheet()

    fun resetFilters()
}