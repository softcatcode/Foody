package com.softcat.foody.navigation.profile

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.softcat.foody.screens.authorization.AuthComponent
import com.softcat.foody.screens.profile.ProfileComponent
import com.softcat.foody.screens.scores.ScoresComponent

interface ProfileRoot {

    val stack: Value<ChildStack<*, Child>>

    sealed interface Child {
        data class Authorization(val component: AuthComponent): Child

        data class Profile(val component: ProfileComponent): Child

        data class Scores(val component: ScoresComponent): Child
    }
}