package com.softcat.foody.screens.scores

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

class ScoresComponentImpl @AssistedInject constructor(
    @Assisted("context") componentContext: ComponentContext,
    @Assisted("back") private val onBackClick: () -> Unit,
    @Assisted("uid") private val userId: String,
    private val storeFactory: ScoresStoreFactory,
): ScoresComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { storeFactory.create(userId, componentContext.lifecycle) }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<ScoresStore.State> = store.stateFlow

    override fun back() {
        Timber.i("${this::class.simpleName}: back()")
        onBackClick()
    }

    override fun remove(recipeId: Int) {
        Timber.i("${this::class.simpleName}: remove($recipeId)")
        store.accept(ScoresStore.Intent.Remove(recipeId))
    }

    override fun changeScoreValue(recipeId: Int, newValue: Int) {
        Timber.i("${this::class.simpleName}: changeScoreValue($recipeId, $newValue)")
        store.accept(ScoresStore.Intent.ChangeValue(recipeId, newValue))
    }

    override fun changeFavouriteStatus(recipeId: Int, isFavourite: Boolean) {
        Timber.i("${this::class.simpleName}: changeFavouriteStatus($recipeId, $isFavourite)")
        store.accept(ScoresStore.Intent.ChangeFavouriteStatus(recipeId, isFavourite))
    }

    override fun changeIsCookedFilter(newValue: Boolean) {
        Timber.i("${this::class.simpleName}: changeIsCookedFilter($newValue)")
        store.accept(ScoresStore.Intent.ChangeIsCookedFilter(newValue))
    }


    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("context") componentContext: ComponentContext,
            @Assisted("back") onBackClick: () -> Unit,
            @Assisted("uid") userId: String
        ): ScoresComponentImpl
    }
}