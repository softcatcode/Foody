package com.softcat.foody.navigation.search

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.softcat.foody.screens.details.DetailsComponent
import com.softcat.foody.screens.search.SearchComponent

interface SearchRoot {
    val stack: Value<ChildStack<*, Child>>

    sealed interface Child {

        data class Search(val component: SearchComponent): Child

        data class Details(val component: DetailsComponent): Child
    }
}