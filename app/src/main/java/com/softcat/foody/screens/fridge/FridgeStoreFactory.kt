package com.softcat.foody.screens.fridge

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.softcat.domain.entities.Ingredient
import com.softcat.domain.usecases.IngredientUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class FridgeStoreFactory @Inject constructor(
    private val ingredientUseCase: IngredientUseCase,
    private val storeFactory: StoreFactory
) {
    fun create(): FridgeStore =
        object: FridgeStore, Store<FridgeStore.Intent, FridgeStore.State, Nothing> by
            storeFactory.create(
                name = this::class.simpleName,
                initialState = FridgeStore.State(
                    categories = emptyList(),
                    dialogState = FridgeStore.State.SelectIngredientDialogState.Hidden,
                ),
                executorFactory = ::FridgeExecutor,
                reducer = FridgeReducer,
                bootstrapper = FridgeBootstrapper()
            ) {}

    private inner class FridgeBootstrapper: CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch(Dispatchers.IO) {
                ingredientUseCase.getAvailableIngredients().collectLatest { ingredients ->
                    withContext(Dispatchers.Main) {
                        dispatch(Action.AvailableIngredientsUpdate(ingredients))
                    }
                }
            }
        }
    }

    private sealed interface Action {
        data class AvailableIngredientsUpdate(val ingredients: List<Ingredient>): Action
    }

    private sealed interface Msg {
        data class AvailableIngredientsUpdate(val ingredients: List<Ingredient>): Msg

        data object ShowDialog: Msg
        data object HideDialog: Msg
    }

    private object FridgeReducer: Reducer<FridgeStore.State, Msg> {
        override fun FridgeStore.State.reduce(msg: Msg): FridgeStore.State {
            Timber.i("${this::class.simpleName}.reduce($msg)")
            return when (msg) {
                is Msg.AvailableIngredientsUpdate -> {
                    val categories = IngredientCategoryToIngredientCardMapper()
                        .mapIngredientsToCategories(msg.ingredients)
                    copy(categories = categories)
                }
                Msg.HideDialog -> copy(
                    dialogState = FridgeStore.State.SelectIngredientDialogState.Hidden
                )
                Msg.ShowDialog -> copy(
                    dialogState = FridgeStore.State.SelectIngredientDialogState.Shown(
                        query = "",
                        searchResult = emptyList()
                    )
                )
            }
        }
    }

    private inner class FridgeExecutor: CoroutineExecutor<FridgeStore.Intent, Action, FridgeStore.State, Msg, Nothing>() {

        private var cachedIngredients: MutableList<Ingredient>? = null

        override fun executeAction(action: Action) {
            Timber.i("${this::class.simpleName}.executeAction($action)")
            when (action) {
                is Action.AvailableIngredientsUpdate -> {
                    cachedIngredients = action.ingredients.toMutableList()
                    dispatch(Msg.AvailableIngredientsUpdate(action.ingredients))
                }
            }
        }

        override fun executeIntent(intent: FridgeStore.Intent) {
            Timber.i("${this::class.simpleName}.executeIntent($intent)")
            when (intent) {
                FridgeStore.Intent.AddIngredientClick -> dispatch(Msg.ShowDialog)
                is FridgeStore.Intent.RemoveIngredient -> removeIngredient(intent.name)
                FridgeStore.Intent.Reset -> resetIngredients()
            }
        }

        private fun removeIngredient(name: String) {
            cachedIngredients?.let { ingredients ->
                val ids = ingredients
                    .asSequence()
                    .filter { it.name != name }
                    .map { it.id }
                    .toList()
                scope.launch(Dispatchers.IO) {
                    ingredientUseCase.setAvailableIngredients(ids)
                }
            }
        }

        private fun resetIngredients() {
            scope.launch(Dispatchers.IO) {
                ingredientUseCase.resetAvailableIngredients()
            }
        }
    }
}