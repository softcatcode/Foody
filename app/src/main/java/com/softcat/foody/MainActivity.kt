package com.softcat.foody

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import com.softcat.foody.navigation.onboarding.OnboardingScreen
import com.softcat.foody.ui.theme.FoodyTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val daggerComponent = (applicationContext as FoodyApplication).component
        val rootComponent = daggerComponent
            .getRootComponentFactory()
            .create(defaultComponentContext())

        setContent {
            FoodyTheme {
                OnboardingScreen(rootComponent)
            }
        }
    }
}