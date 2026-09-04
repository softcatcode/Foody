package com.softcat.foody.screens.fridge

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.softcat.foody.screens.fridge.FridgeStore.Intent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

class FridgeComponentImpl @AssistedInject constructor(
    @Assisted("openShoppingList") private val openShoppingListCallback: () -> Unit,
    @Assisted("back") private val backCallback: () -> Unit,
    @Assisted("context") private val componentContext: ComponentContext,
    private val storeFactory: FridgeStoreFactory
): FridgeComponent, ComponentContext by componentContext {

    private val store: FridgeStore = instanceKeeper.getStore { storeFactory.create() }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<FridgeStore.State> = store.stateFlow

    override fun resetIngredients() {
        Timber.i("${this::class.simpleName}: resetIngredients()")
        store.accept(Intent.Reset)
    }

    override fun addIngredientClick() {
        Timber.i("${this::class.simpleName}: addIngredientClick()")
        store.accept(Intent.AddIngredientClick)
    }

    override fun openShoppingList() {
        Timber.i("${this::class.simpleName}: openCart()")
        openShoppingListCallback()
    }

    override fun openCart() {

    }

    override fun submitIngredient(ingredientId: Int) {
        Timber.i("${this::class.simpleName}: submitIngredient($ingredientId)")
    }

    override fun back() {
        Timber.i("${this::class.simpleName}: back()")
        backCallback()
    }

    override fun removeIngredient(name: String) {
        Timber.i("${this::class.simpleName}: removeIngredient($name)")
        store.accept(Intent.RemoveIngredient(name))
    }

    override fun showDialog() {
        Timber.i("${this::class.simpleName}: showDialog()")
        store.accept(Intent.ShowDialog)
    }

    override fun hideDialog() {
        Timber.i("${this::class.simpleName}: hideDialog()")
        store.accept(Intent.HideDialog)
    }

    override fun addIngredient(name: String) {
        Timber.i("${this::class.simpleName}: addIngredient($name)")
        store.accept(Intent.AddIngredient(name))
    }

    override fun changeSearchQuery(query: String) {
        Timber.i("${this::class.simpleName}: changeSearchQuery($query)")
        store.accept(Intent.ChangeSearchQuery(query))
    }

    override fun searchIngredient(query: String) {
        Timber.i("${this::class.simpleName}: searchIngredient($query)")
        store.accept(Intent.SearchIngredient(query))
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("openShoppingList") openShoppingListCallback: () -> Unit,
            @Assisted("back") back: () -> Unit,
            @Assisted("context") componentContext: ComponentContext,
        ): FridgeComponentImpl
    }
}