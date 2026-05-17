package com.softcat.foody.navigation.onboarding

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import com.softcat.foody.navigation.main.FoodyRootComponentImpl
import com.softcat.foody.screens.initialization.InitializationComponentImpl
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.serialization.Serializable

class OnboardingRootComponentImpl @AssistedInject constructor(
    @Assisted("context") private val componentContext: ComponentContext,
    private val rootComponentFactory: FoodyRootComponentImpl.Factory,
    private val initComponentFactory: InitializationComponentImpl.Factory
): OnboardingRootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, OnboardingRootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Init,
            key = "OnboardingChildStack",
            handleBackButton = true,
            childFactory = ::child
        )

    private fun child(config: Config, componentContext: ComponentContext): OnboardingRootComponent.Child {
        return when (config) {
            Config.FoodyRoot -> {
                val component = rootComponentFactory.create(componentContext)
                OnboardingRootComponent.Child.FoodyRoot(component)
            }
            Config.Init -> {
                val component = initComponentFactory.create(
                    componentContext = componentContext,
                    openFoodyRootScreen = { navigation.replaceCurrent(Config.FoodyRoot) }
                )
                OnboardingRootComponent.Child.Initialization(component)
            }
        }
    }

    @Serializable
    sealed interface Config {

        @Serializable
        data object FoodyRoot: Config

        @Serializable
        data object Init: Config
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("context") componentContext: ComponentContext
        ): OnboardingRootComponentImpl
    }
}