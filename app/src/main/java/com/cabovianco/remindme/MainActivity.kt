package com.cabovianco.remindme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.cabovianco.remindme.presentation.navigation.AppNavigation
import com.cabovianco.remindme.presentation.ui.theme.RemindMeTheme
import com.cabovianco.remindme.presentation.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            viewModel.initialDestination.value == null
        }

        enableEdgeToEdge()
        setContent {
            RemindMeTheme {
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}
