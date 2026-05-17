package com.softcat.foody.navigation.recommend

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.softcat.foody.screens.details.DetailsContent
import com.softcat.foody.screens.recomend.RecommendScreen

@Composable
fun RecommendRootContent(component: RecommendRoot) {
    Children(component.stack) {
        when (val instance = it.instance) {
            is RecommendRoot.Child.Details -> DetailsContent(instance.component)
            is RecommendRoot.Child.Recommend -> RecommendScreen(instance.component)
        }
    }
}