package com.softcat.data.di

import com.softcat.database.facade.Database
import com.softcat.database.facade.DatabaseFacade
import com.softcat.database.remote.implementations.AvatarsManagerImpl
import com.softcat.database.remote.implementations.FavouritesManagerImpl
import com.softcat.database.remote.implementations.InitializeManagerImpl
import com.softcat.database.remote.implementations.ScoresManagerImpl
import com.softcat.database.remote.implementations.UsersManagerImpl
import com.softcat.database.remote.interfaces.AvatarsManager
import com.softcat.database.remote.interfaces.FavouritesManager
import com.softcat.database.remote.interfaces.InitializeManager
import com.softcat.database.remote.interfaces.ScoreManager
import com.softcat.database.remote.interfaces.UsersManager
import dagger.Binds
import dagger.Module

@Module
interface DatabaseModule {
    @Binds
    fun bindAvatarsManager(impl: AvatarsManagerImpl): AvatarsManager

    @Binds
    fun bindFavouritesManager(impl: FavouritesManagerImpl): FavouritesManager

    @Binds
    fun bindUsersManager(impl: UsersManagerImpl): UsersManager

    @Binds
    fun bindScoreManager(impl: ScoresManagerImpl): ScoreManager

    @Binds
    fun bindInitializeManager(impl: InitializeManagerImpl): InitializeManager

    @Binds
    fun bindDatabaseFacade(impl: Database): DatabaseFacade
}