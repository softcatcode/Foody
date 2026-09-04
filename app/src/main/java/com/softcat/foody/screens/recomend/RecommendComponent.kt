package com.softcat.foody.screens.recomend

import com.softcat.domain.entities.RecipeTag
import kotlinx.coroutines.flow.StateFlow

interface RecommendComponent {

    val model: StateFlow<RecommendStore.State>

    fun changeMaxAbsentIngredients(newValue: Int)

    fun removeIngredient(name: String)

    fun addTag(tag: RecipeTag)

    fun removeTag(name: String)

    fun recommend()

    fun showAddTagDialog()

    fun hideDialog()

    fun openRecipeDetails(recipeId: Int)

    fun openFridge()

    fun changeFavouriteStatus(recipeId: Int)

    fun searchTags(query: String)

    fun changeSearchTagQuery(newValue: String)
}