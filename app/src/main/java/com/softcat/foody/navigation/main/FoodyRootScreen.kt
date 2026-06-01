package com.softcat.foody.navigation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.pages.ChildPages
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.softcat.foody.common.NavigationBar
import com.softcat.foody.navigation.favourites.FavouritesRootContent
import com.softcat.foody.navigation.profile.ProfileRootContent
import com.softcat.foody.navigation.recommend.RecommendRootContent
import com.softcat.foody.navigation.search.SearchRootContent

@Composable
fun FoodyRootScreen(component: FoodyRootComponent) {
    val pages by component.pages.subscribeAsState()
    val index = pages.selectedIndex

    Scaffold(
        bottomBar = {
            NavigationBar(
                selectedIndex = index,
                onIndexSelected = component::selectPage
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            ChildPages(
                pages = pages,
                onPageSelected = {}
            ) { _, page ->
                when (page) {
                    is FoodyRootComponent.Child.FavouritesNavComponent -> FavouritesRootContent(page.component)
                    is FoodyRootComponent.Child.ProfileNavComponent -> ProfileRootContent(page.component)
                    is FoodyRootComponent.Child.RecommendNavComponent -> RecommendRootContent(page.component)
                    is FoodyRootComponent.Child.SearchNavComponent -> SearchRootContent(page.component)
                }
            }
        }
    }
}