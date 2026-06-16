package com.softcat.foody.screens.initialization

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.softcat.foody.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class InitializationComponentImpl @AssistedInject constructor(
    @Assisted("context") private val componentContext: ComponentContext,
    @Assisted("navigate") private val openFoodyRootScreen: () -> Unit,
    private val initializationStoreFactory: InitializationStoreFactory,
    private val application: Application
): InitializationComponent, ComponentContext by componentContext {

    private val store = componentContext.instanceKeeper.getStore {
        initializationStoreFactory.create()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<InitializationStore.State> = store.stateFlow

    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        scope.launch {
            store.labels.collect(::labelCollector)
        }
    }

    private fun labelCollector(label: InitializationStore.Label) {
        when (label) {
            InitializationStore.Label.Initialized -> openFoodyRootScreen()
            is InitializationStore.Label.Error -> {
                Toast.makeText(application, label.msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun initRecipes(context: Context) {
        Timber.i("${this::class.simpleName}: initRecipes()")
        store.accept(InitializationStore.Intent.InitRecipes)
    }

    override fun selectOption(value: Int) {
        Timber.i("${this::class.simpleName}: selectOption($value)")
        store.accept(InitializationStore.Intent.SelectOption(value))
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("context") componentContext: ComponentContext,
            @Assisted("navigate") openFoodyRootScreen: () -> Unit
        ): InitializationComponentImpl
    }
}