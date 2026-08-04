package com.cabovianco.remindme.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.cabovianco.remindme.domain.model.InitialDestination
import com.cabovianco.remindme.presentation.ui.screen.AddReminderScreen
import com.cabovianco.remindme.presentation.ui.screen.CreateTagScreen
import com.cabovianco.remindme.presentation.ui.screen.EditReminderScreen
import com.cabovianco.remindme.presentation.ui.screen.MainScreen
import com.cabovianco.remindme.presentation.ui.screen.PermissionScreen
import com.cabovianco.remindme.presentation.ui.screen.WelcomeScreen
import com.cabovianco.remindme.presentation.viewmodel.AddReminderViewModel
import com.cabovianco.remindme.presentation.viewmodel.AppViewModel
import com.cabovianco.remindme.presentation.viewmodel.CreateTagViewModel
import com.cabovianco.remindme.presentation.viewmodel.EditReminderViewModel
import com.cabovianco.remindme.presentation.viewmodel.MainViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: AppViewModel = hiltViewModel()
) {
    val initialDestination by viewModel.initialDestination.collectAsStateWithLifecycle()

    val destination = initialDestination ?: return
    val startDestination: Screen = remember {
        when (destination) {
            InitialDestination.Welcome -> Screen.WelcomeScreen
            InitialDestination.Permission -> Screen.PermissionScreen
            InitialDestination.Main -> Screen.MainScreen
        }
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
                    viewModel.dismissWelcomeScreen()

                    navController.navigate(Screen.PermissionScreen) {
                        popUpTo<Screen.WelcomeScreen> { inclusive = true }
                    }
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
            PermissionScreen(
                onAccept = {
                    navController.navigate(Screen.MainScreen) {
                        popUpTo<Screen.WelcomeScreen> { inclusive = true }
                    }
                }
            )
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
                if (targetState.destination.hasRoute<Screen.CreateTagScreen>()) ExitTransition.KeepUntilTransitionsFinished else
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
                if (targetState.destination.hasRoute<Screen.CreateTagScreen>()) ExitTransition.KeepUntilTransitionsFinished else
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
                if (initialState.destination.hasRoute<Screen.MainScreen>() ||
                    initialState.destination.hasRoute<Screen.AddReminderScreen>() ||
                    initialState.destination.hasRoute<Screen.EditReminderScreen>()
                ) slideIntoContainer(
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
