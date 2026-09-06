package com.softcat.foody.screens.recomend

import com.arkivanov.decompose.value.Value
import com.softcat.domain.entities.RecipeTag

interface RecommendComponent {

    val model: Value<RecommendStore.State>

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