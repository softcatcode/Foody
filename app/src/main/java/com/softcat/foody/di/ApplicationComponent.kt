package com.softcat.foody.di

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.recommender.di.RecommenderModule
import com.softcat.data.di.ApplicationScope
import com.softcat.data.di.DaoModule
import com.softcat.data.di.DatabaseModule
import com.softcat.foody.navigation.onboarding.OnboardingRootComponentImpl
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        RepositoryModule::class,
        DatabaseModule::class,
        DaoModule::class,
        MVIModule::class,
        RecommenderModule::class
    ]
)
@ApplicationScope
interface ApplicationComponent {
    fun getRootComponentFactory(): OnboardingRootComponentImpl.Factory

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application,
            @BindsInstance datastore: DataStore<Preferences>
        ): ApplicationComponent
    }
}