package com.softcat.foody.screens.authorization

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.softcat.domain.entities.User
import com.softcat.domain.usecases.UserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class AuthStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val userUseCase: UserUseCase,
) {

    fun create(): AuthStore =
        object:
            AuthStore, Store<AuthStore.Intent, AuthStore.State, AuthStore.Label>
        by
            storeFactory.create(
                name = this::class.simpleName,
                initialState = AuthStore.State.Initial,
                bootstrapper = AuthBootstrapper(),
                executorFactory = ::AuthExecutor,
                reducer = AuthReducer
            ) {}

    private sealed interface Action {
        data class AlreadyAuthorized(val user: User): Action

        data object NoUser: Action
    }

    private inner class AuthBootstrapper: CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch(Dispatchers.IO) {
                userUseCase.observeLastEnteredUser().collect {
                    withContext(Dispatchers.Main) {
                        if (it != null)
                            dispatch(Action.AlreadyAuthorized(it))
                        else
                            dispatch(Action.NoUser)
                    }
                }
            }
        }
    }

    sealed interface Msg {
        data class ChangeName(val newValue: String): Msg
        data class ChangeEmail(val newValue: String): Msg
        data class ChangePassword(val newValue: String): Msg
        data class ChangeRepeatedPassword(val newValue: String): Msg
        data object SwitchToEnter: Msg
        data object SwitchToRegister: Msg
        data object SwitchToInitialScreen: Msg
        data object StartLoading: Msg
        data object StopLoading: Msg
    }

    private inner class AuthExecutor: CoroutineExecutor<AuthStore.Intent, Action, AuthStore.State, Msg, AuthStore.Label>() {
        override fun executeIntent(intent: AuthStore.Intent) {
            Timber.i("${this::class.simpleName}: Intent is obtained: $intent")
            when (intent) {
                is AuthStore.Intent.ChangeEmail -> dispatch(Msg.ChangeEmail(intent.newValue))
                is AuthStore.Intent.ChangeName -> dispatch(Msg.ChangeName(intent.newValue))
                is AuthStore.Intent.ChangePassword -> dispatch(Msg.ChangePassword(intent.newValue))
                is AuthStore.Intent.ChangeRepeatedPassword -> dispatch(Msg.ChangeRepeatedPassword(intent.newValue))
                AuthStore.Intent.SwitchToEnter -> dispatch(Msg.SwitchToEnter)
                AuthStore.Intent.SwitchToRegister -> dispatch(Msg.SwitchToRegister)

                AuthStore.Intent.Enter -> {
                    dispatch(Msg.StartLoading)
                    scope.launch(Dispatchers.IO) {
                        val data = state() as? AuthStore.State.Enter ?: return@launch
                        userUseCase.enter(data.email, data.password).onSuccess { user ->
                            withContext(Dispatchers.Main) {
                                dispatch(Msg.StopLoading)
                                publish(AuthStore.Label.Entered(user))
                            }
                        }.onFailure {
                            withContext(Dispatchers.Main) {
                                dispatch(Msg.StopLoading)
                                publish(AuthStore.Label.Error(it.message))
                            }
                        }
                    }
                }

                AuthStore.Intent.Register -> {
                    val data = state() as? AuthStore.State.Register ?: return
                    dispatch(Msg.StartLoading)
                    scope.launch(Dispatchers.IO) {
                        userUseCase.register(
                            data.name, data.email, data.password to data.repeatPassword
                        ).onSuccess { user ->
                            withContext(Dispatchers.Main) {
                                dispatch(Msg.StopLoading)
                                publish(AuthStore.Label.Registered(user))
                            }
                        }.onFailure {
                            withContext(Dispatchers.Main) {
                                dispatch(Msg.StopLoading)
                                publish(AuthStore.Label.Error(it.message))
                            }
                        }
                    }
                }

                AuthStore.Intent.SwitchToInitialScreen -> {
                    val notInitial = state().let {
                        it is AuthStore.State.Enter || it is AuthStore.State.Register
                    }
                    if (notInitial)
                        dispatch(Msg.SwitchToInitialScreen)
                }
            }
        }

        override fun executeAction(action: Action) {
            Timber.i("${this::class.simpleName}: Action is obtained: $action")
            when (action) {
                is Action.AlreadyAuthorized -> publish(AuthStore.Label.Entered(action.user))
                Action.NoUser -> dispatch(Msg.SwitchToInitialScreen)
            }
        }
    }

    private object AuthReducer: Reducer<AuthStore.State, Msg> {
        override fun AuthStore.State.reduce(msg: Msg): AuthStore.State {
            Timber.i("${this::class.simpleName}: Message is obtained: $msg")
            return when (msg) {
                is Msg.ChangeEmail -> {
                    when (this) {
                        is AuthStore.State.Enter -> copy(email = msg.newValue)
                        is AuthStore.State.Register -> copy(email = msg.newValue)
                        else -> this
                    }
                }

                is Msg.ChangeName -> {
                    if (this is AuthStore.State.Register)
                        copy(name = msg.newValue)
                    else
                        this
                }

                is Msg.ChangePassword -> {
                    when (this) {
                        is AuthStore.State.Enter -> copy(password = msg.newValue)
                        is AuthStore.State.Register -> copy(password = msg.newValue)
                        else -> this
                    }
                }

                is Msg.ChangeRepeatedPassword -> {
                    if (this is AuthStore.State.Register)
                        copy(repeatPassword = msg.newValue)
                    else
                        this
                }

                Msg.SwitchToEnter -> {
                    val data = this as? AuthStore.State.Register
                    AuthStore.State.Enter(
                        email = data?.email ?: "",
                        password = data?.password ?: "",
                        isLoading = false
                    )
                }

                Msg.SwitchToRegister -> {
                    val data = this as? AuthStore.State.Enter
                    AuthStore.State.Register(
                        name = "",
                        email = data?.email ?: "",
                        password = data?.password ?: "",
                        repeatPassword = "",
                        isLoading = false
                    )
                }

                Msg.SwitchToInitialScreen -> AuthStore.State.NoUser

                Msg.StartLoading -> {
                    when (this) {
                        is AuthStore.State.Register -> copy(isLoading = true)
                        is AuthStore.State.Enter -> copy(isLoading = true)
                        else -> this
                    }
                }

                Msg.StopLoading -> {
                    when (this) {
                        is AuthStore.State.Register -> copy(isLoading = false)
                        is AuthStore.State.Enter -> copy(isLoading = false)
                        else -> this
                    }
                }
            }
        }
    }
}