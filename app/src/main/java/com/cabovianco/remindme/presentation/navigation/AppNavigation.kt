package com.cabovianco.remindme.presentation.navigation

import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cabovianco.remindme.presentation.ui.screen.WelcomeScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        modifier = modifier
    ) { padding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    val navToPermissionScreen = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screen.WelcomeScreen.route
    ) {
        composable(route = Screen.WelcomeScreen.route) {
            WelcomeScreen(
                onGetStartedClick = {
                    navController.navigate(
                        if (navToPermissionScreen) Screen.PermissionScreen.route
                        else Screen.MainScreen.route
                    )
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(route = Screen.PermissionScreen.route) {

        }

        composable(route = Screen.MainScreen.route) {

        }

        composable(route = Screen.AddReminderScreen.route) {

        }

        composable(route = Screen.EditReminderScreen.route) {

        }
    }
}
