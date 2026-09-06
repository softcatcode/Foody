package com.softcat.foody.navigation.recommend

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.softcat.foody.screens.details.DetailsComponent
import com.softcat.foody.screens.fridge.FridgeComponent
import com.softcat.foody.screens.recomend.RecommendComponent

interface RecommendRoot {
    val stack: Value<ChildStack<*, Child>>

    sealed interface Child {
        data class Recommend(val component: RecommendComponent): Child
        data class Details(val component: DetailsComponent): Child

        data class Fridge(val component: FridgeComponent): Child
    }
}