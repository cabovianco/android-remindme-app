package com.cabovianco.remindme.presentation.navigation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.cabovianco.remindme.presentation.ui.screen.AddReminderScreen
import com.cabovianco.remindme.presentation.ui.screen.CreateTagScreen
import com.cabovianco.remindme.presentation.ui.screen.EditReminderScreen
import com.cabovianco.remindme.presentation.ui.screen.MainScreen
import com.cabovianco.remindme.presentation.ui.screen.PermissionScreen
import com.cabovianco.remindme.presentation.ui.screen.WelcomeScreen
import com.cabovianco.remindme.presentation.viewmodel.AddReminderViewModel
import com.cabovianco.remindme.presentation.viewmodel.CreateTagViewModel
import com.cabovianco.remindme.presentation.viewmodel.EditReminderViewModel
import com.cabovianco.remindme.presentation.viewmodel.MainViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val showWelcomeScreen = prefs.getBoolean("showWelcomeScreen", false)

    val navToPermissionScreen = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    var isPermissionAccepted by remember { mutableStateOf(true) }

    if (navToPermissionScreen) {
        isPermissionAccepted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    val startDestination: Screen = when {
        !showWelcomeScreen -> Screen.WelcomeScreen
        navToPermissionScreen && !isPermissionAccepted -> Screen.PermissionScreen
        else -> Screen.MainScreen
    }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable<Screen.WelcomeScreen> {
            WelcomeScreen(
                onGetStartedClick = {
                    prefs.edit(commit = true) { putBoolean("showWelcomeScreen", true) }
                    navController.navigate(
                        if (navToPermissionScreen) Screen.PermissionScreen
                        else Screen.MainScreen
                    )
                }
            )
        }

        composable<Screen.PermissionScreen>(
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
            },
            exitTransition = {
                if (targetState.destination.hasRoute<Screen.MainScreen>()) ExitTransition.None else
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
            }
        ) {
            if (navToPermissionScreen) {
                PermissionScreen(
                    onAccept = {
                        navController.navigate(Screen.MainScreen) {
                            popUpTo<Screen.WelcomeScreen> { inclusive = true }
                        }
                    }
                )
            }
        }

        composable<Screen.MainScreen>(
            enterTransition = {
                if (initialState.destination.hasRoute<Screen.PermissionScreen>()) slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left
                ) else EnterTransition.None
            },
            exitTransition = { ExitTransition.KeepUntilTransitionsFinished }
        ) {
            val viewModel = hiltViewModel<MainViewModel>()

            MainScreen(
                onAddReminder = { navController.navigate(Screen.AddReminderScreen) },
                onEditReminder = { navController.navigate(Screen.EditReminderScreen(it)) },
                onCreateTag = { navController.navigate(Screen.CreateTagScreen) },
                viewModel = viewModel
            )
        }

        composable<Screen.AddReminderScreen>(
            enterTransition = {
                if (initialState.destination.hasRoute<Screen.MainScreen>()) slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left
                ) else EnterTransition.None
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
            }
        ) {
            val viewModel: AddReminderViewModel = hiltViewModel()

            AddReminderScreen(
                onBackClick = { navController.navigateUp() },
                onCreateTag = { navController.navigate(Screen.CreateTagScreen) },
                viewModel = viewModel
            )
        }

        composable<Screen.EditReminderScreen>(
            enterTransition = {
                if (initialState.destination.hasRoute<Screen.MainScreen>()) slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left
                ) else EnterTransition.None
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
            }
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.EditReminderScreen>()
            val viewModel: EditReminderViewModel = hiltViewModel()

            EditReminderScreen(
                reminderId = route.id,
                onBackClick = { navController.navigateUp() },
                onCreateTag = { navController.navigate(Screen.CreateTagScreen) },
                viewModel = viewModel
            )
        }

        composable<Screen.CreateTagScreen>(
            enterTransition = {
                if (initialState.destination.hasRoute<Screen.MainScreen>()) slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left
                ) else EnterTransition.None
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
            }
        ) {
            val viewModel: CreateTagViewModel = hiltViewModel()

            CreateTagScreen(
                onBackClick = { navController.navigateUp() },
                viewModel = viewModel
            )
        }
    }
}
