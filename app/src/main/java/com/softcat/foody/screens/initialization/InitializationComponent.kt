package com.softcat.foody.screens.initialization

import android.content.Context
import kotlinx.coroutines.flow.StateFlow

interface InitializationComponent {

    val model: StateFlow<InitializationStore.State>

    fun initRecipes(context: Context)

    fun selectOption(value: Int)
}