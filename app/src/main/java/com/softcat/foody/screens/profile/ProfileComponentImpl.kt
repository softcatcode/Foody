package com.softcat.foody.screens.profile

import android.content.Context
import android.net.Uri
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.softcat.domain.entities.User
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class ProfileComponentImpl @AssistedInject constructor(
    private val storeFactory: ProfileStoreFactory,
    @Assisted("context") private val componentContext: ComponentContext,
    @Assisted("scores") private val openScoresCallback: (String) -> Unit,
    @Assisted("scores") private val onExitCallback: () -> Unit,
    @Assisted("user") private val user: User
): ProfileComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { storeFactory.create(user) }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<ProfileStore.State> = store.stateFlow

    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        scope.launch {
            store.labels.collect(::labelsColletor)
        }
    }

    private fun labelsColletor(label: ProfileStore.Label) {
        Timber.i("${this::class.simpleName}: Label is obtained: $label")
        when (label) {
            ProfileStore.Label.Exited -> onExitCallback()
            is ProfileStore.Label.OpenScores -> openScoresCallback(label.userId)
        }
    }

    override fun exit() {
        Timber.i("${this::class.simpleName}: exit()")
        store.accept(ProfileStore.Intent.Exit)
    }

    override fun openScores() {
        Timber.i("${this::class.simpleName}: openScores()")
        store.accept(ProfileStore.Intent.LoadScores)
    }

    override fun modifyUser(user: User) {
        Timber.i("${this::class.simpleName}: modifyUser()")
        store.accept(ProfileStore.Intent.ModifyUser(user))
    }

    override fun saveAvatar(context: Context, uri: Uri?) {
        Timber.i("${this::class.simpleName}.saveAvatar($uri)")
        store.accept(ProfileStore.Intent.SaveAvatar(uri))
    }

    override fun showExitDialog() {
        Timber.i("${this::class.simpleName}.showExitDialog()")
        store.accept(ProfileStore.Intent.ShowDialog)
    }

    override fun hideExitDialog() {
        Timber.i("${this::class.simpleName}.hideExitDialog()")
        store.accept(ProfileStore.Intent.HideDialog)
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("context")componentContext: ComponentContext,
            @Assisted("scores") openScoresCallback: (String) -> Unit,
            @Assisted("scores") onExitCallback: () -> Unit,
            @Assisted("user") user: User,
        ): ProfileComponentImpl
    }
}