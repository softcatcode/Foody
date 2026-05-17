package com.softcat.foody.navigation.search

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.softcat.foody.screens.details.DetailsContent
import com.softcat.foody.screens.search.SearchScreen

@Composable
fun SearchRootContent(component: SearchRoot) {
    Children(component.stack) {
        when (val instance = it.instance) {
            is SearchRoot.Child.Details -> DetailsContent(instance.component)
            is SearchRoot.Child.Search -> SearchScreen(instance.component)
        }
    }
}