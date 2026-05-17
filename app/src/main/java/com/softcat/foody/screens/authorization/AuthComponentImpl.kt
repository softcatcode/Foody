package com.softcat.foody.screens.authorization

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
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import timber.log.Timber

class AuthComponentImpl @AssistedInject constructor(
    @Assisted("context") componentContext: ComponentContext,
    @Assisted("register") private val onRegistered: (User) -> Unit,
    @Assisted("enter") private val onEntered: (User) -> Unit,
    @Assisted("error") private val onError: (String?) -> Unit,
    private val storeFactory: AuthStoreFactory
): AuthComponent, ComponentContext by componentContext {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val store = instanceKeeper.getStore { storeFactory.create() }

    private val labelsSharedFlow = store.labels
        .shareIn(
            scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob()),
            started = SharingStarted.Eagerly,
            replay = 1
        )

    init {
        scope.launch(Dispatchers.Main) {
            labelsSharedFlow.collect(::labelCollector)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<AuthStore.State> = store.stateFlow

    private fun labelCollector(label: AuthStore.Label) {
        Timber.i("${this::class.simpleName}: Label is obtained: $label")
        when (label) {
            is AuthStore.Label.Entered -> onEntered(label.user)
            is AuthStore.Label.Registered -> onRegistered(label.user)
            is AuthStore.Label.Error -> onError(label.msg)
        }
    }

    override fun changeName(newValue: String) {
        Timber.i("${this::class.simpleName}: changeName($newValue)")
        store.accept(AuthStore.Intent.ChangeName(newValue))
    }

    override fun changeEmail(newValue: String) {
        Timber.i("${this::class.simpleName}: changeEmail($newValue)")
        store.accept(AuthStore.Intent.ChangeEmail(newValue))
    }

    override fun changePassword(newValue: String) {
        Timber.i("${this::class.simpleName}: changePassword($newValue)")
        store.accept(AuthStore.Intent.ChangePassword(newValue))
    }

    override fun changeRepeatedPassword(newValue: String) {
        Timber.i("${this::class.simpleName}: changeRepeatedPassword($newValue)")
        store.accept(AuthStore.Intent.ChangeRepeatedPassword(newValue))
    }

    override fun enter() {
        Timber.i("${this::class.simpleName}: enter()")
        store.accept(AuthStore.Intent.Enter)
    }

    override fun register() {
        Timber.i("${this::class.simpleName}: register()")
        store.accept(AuthStore.Intent.Register)
    }

    override fun switchToEnter() {
        Timber.i("${this::class.simpleName}: switchToEnter()")
        store.accept(AuthStore.Intent.SwitchToEnter)
    }

    override fun switchToRegister() {
        Timber.i("${this::class.simpleName}: switchToRegister()")
        store.accept(AuthStore.Intent.SwitchToRegister)
    }

    override fun switchToInitialScreen() {
        Timber.i("${this::class.simpleName}: switchToInitialScreen()")
        store.accept(AuthStore.Intent.SwitchToInitialScreen)
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("context") componentContext: ComponentContext,
            @Assisted("register") onRegistered: (User) -> Unit,
            @Assisted("enter") onEntered: (User) -> Unit,
            @Assisted("error") onError: (String) -> Unit,
        ): AuthComponentImpl
    }
}