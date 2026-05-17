package com.softcat.foody.screens.authorization

import kotlinx.coroutines.flow.StateFlow

interface AuthComponent {

    val model: StateFlow<AuthStore.State>

    fun changeName(newValue: String)
    fun changeEmail(newValue: String)
    fun changePassword(newValue: String)
    fun changeRepeatedPassword(newValue: String)

    fun enter()
    fun register()

    fun switchToEnter()
    fun switchToRegister()
    fun switchToInitialScreen()
}