package com.softcat.foody

import android.app.Application
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.FirebaseApp
import com.softcat.foody.di.DaggerApplicationComponent
import kotlin.getValue

class FoodyApplication: Application() {

    private val dataStore by preferencesDataStore(
        name = DATASTORE_NAME
    )

    val component by lazy {
        DaggerApplicationComponent.factory().create(this, dataStore)
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }

    companion object {
        private const val DATASTORE_NAME = "foody_datastore"
    }
}