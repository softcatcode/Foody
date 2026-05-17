package com.softcat.foody.navigation.profile

import android.app.Application
import android.widget.Toast
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import com.softcat.domain.entities.User
import com.softcat.foody.screens.authorization.AuthComponentImpl
import com.softcat.foody.screens.profile.ProfileComponentImpl
import com.softcat.foody.screens.scores.ScoresComponentImpl
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.serialization.Serializable

class ProfileRootImpl @AssistedInject constructor(
    @Assisted("context") componentContext: ComponentContext,
    private val profileComponentFactory: ProfileComponentImpl.Factory,
    private val authComponentFactory: AuthComponentImpl.Factory,
    private val scoresComponentFactory: ScoresComponentImpl.Factory,
    private val application: Application,
): ProfileRoot, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, ProfileRoot.Child>> = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.Authorization,
        key = "ProfileChildStack",
        handleBackButton = true,
        childFactory = ::child
    )

    @OptIn(DelicateDecomposeApi::class)
    private fun child(
        config: Config,
        componentContext: ComponentContext
    ): ProfileRoot.Child {
        return when (config) {
            Config.Authorization -> {
                val component = authComponentFactory.create(
                    componentContext = componentContext,
                    onRegistered = { navigation.replaceCurrent(Config.Profile(it)) },
                    onEntered = { navigation.replaceCurrent(Config.Profile(it)) },
                    onError = ::processError
                )
                ProfileRoot.Child.Authorization(component)
            }

            is Config.Profile -> {
                val component = profileComponentFactory.create(
                    componentContext = componentContext,
                    openScoresCallback = { navigation.push(Config.Scores(it)) },
                    onExitCallback = { navigation.replaceCurrent(Config.Authorization) },
                    user = config.user,
                )
                ProfileRoot.Child.Profile(component)
            }

            is Config.Scores -> {
                val component = scoresComponentFactory.create(
                    componentContext = componentContext,
                    onBackClick = { navigation.pop() },
                    userId = config.userId,
                )
                ProfileRoot.Child.Scores(component)
            }
        }
    }

    private fun processError(msg: String?) {
        Toast.makeText(application, msg ?: "", Toast.LENGTH_SHORT).show()
    }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Authorization: Config

        @Serializable
        data class Profile(val user: User): Config

        @Serializable
        data class Scores(val userId: String): Config
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("context") componentContext: ComponentContext,
        ): ProfileRootImpl
    }
}