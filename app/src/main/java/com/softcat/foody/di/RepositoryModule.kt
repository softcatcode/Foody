package com.softcat.foody.di

import android.content.Context
import com.softcat.data.di.ApplicationScope
import com.softcat.data.implementations.AvatarRepositoryImpl
import com.softcat.data.implementations.FavouritesRepositoryImpl
import com.softcat.data.implementations.IngredientRepositoryImpl
import com.softcat.data.implementations.InitializeRepositoryImpl
import com.softcat.data.implementations.RecipeRepositoryImpl
import com.softcat.data.implementations.RecipeTagRepositoryImpl
import com.softcat.data.implementations.ScoreRepositoryImpl
import com.softcat.data.implementations.UserRepositoryImpl
import com.softcat.domain.interfaces.AvatarRepository
import com.softcat.domain.interfaces.FavouritesRepository
import com.softcat.domain.interfaces.IngredientRepository
import com.softcat.domain.interfaces.InitializeRepository
import com.softcat.domain.interfaces.RecipeRepository
import com.softcat.domain.interfaces.RecipeTagRepository
import com.softcat.domain.interfaces.ScoreRepository
import com.softcat.domain.interfaces.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface RepositoryModule {
    @ApplicationScope
    @Binds
    fun bindAvatarRepository(impl: AvatarRepositoryImpl): AvatarRepository

    @ApplicationScope
    @Binds
    fun bindFavouritesRepository(impl: FavouritesRepositoryImpl): FavouritesRepository

    @ApplicationScope
    @Binds
    fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @ApplicationScope
    @Binds
    fun bindScoreRepository(impl: ScoreRepositoryImpl): ScoreRepository

    @ApplicationScope
    @Binds
    fun bindRecipeRepository(impl: RecipeRepositoryImpl): RecipeRepository

    @ApplicationScope
    @Binds
    fun bindInitializeRepository(impl: InitializeRepositoryImpl): InitializeRepository

    @ApplicationScope
    @Binds
    fun bindRecipeTagRepository(impl: RecipeTagRepositoryImpl): RecipeTagRepository

    @ApplicationScope
    @Binds
    fun bindIngredientRepository(impl: IngredientRepositoryImpl): IngredientRepository

    companion object {
        @Provides
        fun provideRecipeDao(context: Context) = context
    }
}