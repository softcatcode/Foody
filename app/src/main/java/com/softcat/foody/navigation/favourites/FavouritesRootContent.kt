package com.softcat.foody.navigation.favourites

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.softcat.foody.screens.details.DetailsContent
import com.softcat.foody.screens.favourites.FavouritesScreen

@Composable
fun FavouritesRootContent(component: FavouritesRoot) {
    Children(component.stack) {
        when (val instance = it.instance) {
            is FavouritesRoot.Child.Details -> DetailsContent(instance.component)
            is FavouritesRoot.Child.Favourites -> FavouritesScreen(instance.component)
        }
    }
}