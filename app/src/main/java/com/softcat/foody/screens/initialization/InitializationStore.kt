package com.softcat.foody.screens.initialization

import com.arkivanov.mvikotlin.core.store.Store

interface InitializationStore: Store<InitializationStore.Intent, InitializationStore.State, InitializationStore.Label> {

    sealed interface Intent {
        data object InitRecipes: Intent
        data class SelectOption(val value: Int): Intent
    }

    sealed interface State {
        data class Options(
            val options: List<OptionModel>
        ): State

        data object Loading: State
    }

    sealed interface Label {
        data object Initialized: Label

        data class Error(val msg: String): Label
    }
}