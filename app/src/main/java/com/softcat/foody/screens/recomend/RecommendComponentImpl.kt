package com.softcat.foody.screens.recomend

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.softcat.domain.entities.Ingredient
import com.softcat.domain.entities.Recipe
import com.softcat.domain.entities.RecipeTag
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class RecommendComponentImpl @AssistedInject constructor(
    private val storeFactory: RecommendStoreFactory,
    @Assisted("context") componentContext: ComponentContext,
    @Assisted("open_recipe") private val openRecipeDetailsCallback: (Recipe) -> Unit
): RecommendComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { storeFactory.create(componentContext.lifecycle) }
    private val scope = CoroutineScope(Dispatchers.IO)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<RecommendStore.State> = store.stateFlow

    init {
        scope.launch {
            store.labels.collect(::labelCollector)
        }
    }

    private fun labelCollector(label: RecommendStore.Label) {
        when (label) {
            is RecommendStore.Label.OpenRecipeDetails -> openRecipeDetailsCallback(label.recipe)
        }
    }

    override fun changeMaxAbsentIngredients(newValue: Int) {
        Timber.i("${this::class.simpleName}: changeMaxAbsentIngredients()")
        store.accept(RecommendStore.Intent.ChangeMaxAbsentIngredients(newValue))
    }

    override fun addIngredient(ingredient: Ingredient) {
        Timber.i("${this::class.simpleName}: addIngredient()")
        store.accept(RecommendStore.Intent.AddIngredient(ingredient))
    }

    override fun removeIngredient(name: String) {
        Timber.i("${this::class.simpleName}: removeIngredient()")
        store.accept(RecommendStore.Intent.RemoveIngredient(name))
    }

    override fun addTag(tag: RecipeTag) {
        Timber.i("${this::class.simpleName}: addTag()")
        store.accept(RecommendStore.Intent.AddTag(tag))
    }

    override fun removeTag(name: String) {
        Timber.i("${this::class.simpleName}: removeTag()")
        store.accept(RecommendStore.Intent.RemoveTag(name))
    }

    override fun recommend() {
        Timber.i("${this::class.simpleName}: recommend()")
        store.accept(RecommendStore.Intent.Recommend)
    }

    override fun showAddTagDialog() {
        Timber.i("${this::class.simpleName}: showAddTagDialog()")
        store.accept(RecommendStore.Intent.ShowAddRequiredTagDialog)
    }

    override fun showAddIngredientDialog() {
        Timber.i("${this::class.simpleName}: showAddIngredientDialog()")
        store.accept(RecommendStore.Intent.ShowAddRequiredIngredientDialog)
    }

    override fun hideDialog() {
        Timber.i("${this::class.simpleName}: hideDialog()")
        store.accept(RecommendStore.Intent.HideDialog)
    }

    override fun openRecipeDetails(recipeId: Int) {
        Timber.i("${this::class.simpleName}: openRecipeDetails($recipeId)")
        store.accept(RecommendStore.Intent.OpenRecipeDetails(recipeId))
    }

    override fun changeFavouriteStatus(recipeId: Int, isFavourite: Boolean) {
        Timber.i("${this::class.simpleName}: changeFavouriteStatus($recipeId, $isFavourite)")
        store.accept(RecommendStore.Intent.ChangeFavouriteStatus(recipeId, isFavourite))
    }

    override fun searchIngredients(query: String) {
        Timber.i("${this::class.simpleName}: searchIngredients($query)")
        store.accept(RecommendStore.Intent.SearchIngredient(query))
    }

    override fun searchTags(query: String) {
        Timber.i("${this::class.simpleName}: searchTags($query)")
        store.accept(RecommendStore.Intent.SearchTag(query))
    }

    override fun changeSearchTagQuery(newValue: String) {
        Timber.i("${this::class.simpleName}: changeSearchTagQuery($newValue)")
        store.accept(RecommendStore.Intent.ChangeSearchTagQuery(newValue))
    }

    override fun changeSearchIngredientQuery(newValue: String) {
        Timber.i("${this::class.simpleName}: changeSearchIngredientQuery($newValue)")
        store.accept(RecommendStore.Intent.ChangeSearchIngredientQuery(newValue))
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("context") componentContext: ComponentContext,
            @Assisted("open_recipe") openRecipeDetailsCallback: (Recipe) -> Unit
        ): RecommendComponentImpl
    }
}