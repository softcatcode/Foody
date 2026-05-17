package com.softcat.foody.screens.authorization

import com.arkivanov.mvikotlin.core.store.Store
import com.softcat.domain.entities.User

interface AuthStore: Store<AuthStore.Intent, AuthStore.State, AuthStore.Label> {

    sealed interface Intent {
        data class ChangeName(val newValue: String): Intent
        data class ChangeEmail(val newValue: String): Intent
        data class ChangePassword(val newValue: String): Intent
        data class ChangeRepeatedPassword(val newValue: String): Intent

        data object Enter: Intent

        data object Register: Intent

        data object SwitchToEnter: Intent
        data object SwitchToRegister: Intent

        data object SwitchToInitialScreen: Intent
    }

    sealed interface State {
        data object Initial: State

        data object NoUser: State

        data class Enter(
            val email: String,
            val password: String,
            val isLoading: Boolean
        ): State

        data class Register(
            val name: String,
            val email: String,
            val password: String,
            val repeatPassword: String,
            val isLoading: Boolean
        ): State
    }

    sealed interface Label {
        data class Entered(val user: User): Label

        data class Registered(val user: User): Label

        data class Error(val msg: String?): Label
    }
}