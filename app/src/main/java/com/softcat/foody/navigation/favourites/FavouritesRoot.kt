package com.softcat.foody.navigation.favourites

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.softcat.foody.screens.details.DetailsComponent
import com.softcat.foody.screens.favourites.FavouritesComponent

interface FavouritesRoot {

    val stack: Value<ChildStack<*, Child>>

    sealed interface Child {
        data class Favourites(val component: FavouritesComponent): Child

        data class Details(val component: DetailsComponent): Child
    }
}