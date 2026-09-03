package com.softcat.foody.screens.recomend

import com.softcat.domain.entities.Ingredient
import com.softcat.domain.entities.RecipeTag
import kotlinx.coroutines.flow.StateFlow

interface RecommendComponent {

    val model: StateFlow<RecommendStore.State>

    fun changeMaxAbsentIngredients(newValue: Int)

    fun addIngredient(ingredient: Ingredient)

    fun removeIngredient(name: String)

    fun addTag(tag: RecipeTag)

    fun removeTag(name: String)

    fun recommend()

    fun showAddTagDialog()

    fun showAddIngredientDialog()

    fun hideDialog()

    fun openRecipeDetails(recipeId: Int)

    fun openFridge()

    fun changeFavouriteStatus(recipeId: Int)

    fun searchIngredients(query: String)

    fun searchTags(query: String)

    fun changeSearchTagQuery(newValue: String)

    fun changeSearchIngredientQuery(newValue: String)
}