package com.softcat.foody.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dagger.Module
import dagger.Provides

@Module
class MVIModule {

    @Provides
    fun bindStoreFactory(): StoreFactory {
        return DefaultStoreFactory()
    }
}