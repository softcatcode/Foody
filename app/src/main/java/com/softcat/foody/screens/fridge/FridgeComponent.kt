package com.softcat.foody.screens.fridge

import kotlinx.coroutines.flow.StateFlow

interface FridgeComponent {

    val model: StateFlow<FridgeStore.State>

    fun resetIngredients()

    fun addIngredientClick()

    fun openShoppingList()

    fun openCart()

    fun submitIngredient(ingredientId: Int)

    fun back()

    fun removeIngredient(name: String)
}