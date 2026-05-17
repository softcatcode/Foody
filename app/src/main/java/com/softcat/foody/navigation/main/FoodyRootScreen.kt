package com.softcat.foody.navigation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.softcat.foody.common.NavigationBar
import com.softcat.foody.navigation.favourites.FavouritesRootContent
import com.softcat.foody.navigation.profile.ProfileRootContent
import com.softcat.foody.navigation.recommend.RecommendRootContent
import com.softcat.foody.navigation.search.SearchRootContent

@Composable
fun FoodyRootScreen(component: FoodyRootComponent) {
    val pages = component.pages.subscribeAsState()
    val index = pages.value.selectedIndex
    val instance = pages.value.items[index].instance ?: return

    Scaffold(
        bottomBar = {
            NavigationBar(
                selectedIndex = index,
                onIndexSelected = { component.selectPage(it) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (instance) {
                is FoodyRootComponent.Child.FavouritesNavComponent -> FavouritesRootContent(instance.component)
                is FoodyRootComponent.Child.ProfileNavComponent -> ProfileRootContent(instance.component)
                is FoodyRootComponent.Child.RecommendNavComponent -> RecommendRootContent(instance.component)
                is FoodyRootComponent.Child.SearchNavComponent -> SearchRootContent(instance.component)
            }
        }
    }
}