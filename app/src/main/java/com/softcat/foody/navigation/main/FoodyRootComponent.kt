package com.softcat.foody.navigation.main

import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.value.Value
import com.softcat.foody.navigation.favourites.FavouritesRoot
import com.softcat.foody.navigation.profile.ProfileRoot
import com.softcat.foody.navigation.recommend.RecommendRoot
import com.softcat.foody.navigation.search.SearchRoot

interface FoodyRootComponent {

    val pages: Value<ChildPages<*, Child>>

    fun selectPage(index: Int)

    sealed interface Child {
        data class ProfileNavComponent(val component: ProfileRoot): Child

        data class FavouritesNavComponent(val component: FavouritesRoot): Child

        data class SearchNavComponent(val component: SearchRoot): Child

        data class RecommendNavComponent(val component: RecommendRoot): Child
    }
}