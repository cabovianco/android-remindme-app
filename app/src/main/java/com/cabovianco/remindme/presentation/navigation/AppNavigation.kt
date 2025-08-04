package com.cabovianco.remindme.presentation.navigation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cabovianco.remindme.presentation.ui.screen.AddReminderScreen
import com.cabovianco.remindme.presentation.ui.screen.EditReminderScreen
import com.cabovianco.remindme.presentation.ui.screen.MainScreen
import com.cabovianco.remindme.presentation.ui.screen.PermissionScreen
import com.cabovianco.remindme.presentation.ui.screen.WelcomeScreen
import com.cabovianco.remindme.presentation.viewmodel.AddReminderViewModel
import com.cabovianco.remindme.presentation.viewmodel.EditReminderViewModel
import com.cabovianco.remindme.presentation.viewmodel.MainViewModel

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

    val startDestination = when {
        !showWelcomeScreen -> Screen.WelcomeScreen.route
        navToPermissionScreen && !isPermissionAccepted -> Screen.PermissionScreen.route
        else -> Screen.MainScreen.route
    }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.WelcomeScreen.route) {
            WelcomeScreen(
                onGetStartedClick = {
                    prefs.edit(commit = true) { putBoolean("showWelcomeScreen", true) }
                    navController.navigate(
                        if (navToPermissionScreen) Screen.PermissionScreen.route
                        else Screen.MainScreen.route
                    )
                },
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            )
        }

        composable(
            route = Screen.PermissionScreen.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
            }
        ) {
            if (navToPermissionScreen) {
                PermissionScreen(
                    onAccept = {
                        navController.popBackStack(
                            route = Screen.WelcomeScreen.route,
                            inclusive = true
                        )
                        navController.navigate(Screen.MainScreen.route)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                )
            }
        }

        composable(
            route = Screen.MainScreen.route,
            enterTransition = {
                if (initialState.destination.route == Screen.PermissionScreen.route) {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                } else {
                    EnterTransition.None
                }
            },
            exitTransition = { ExitTransition.KeepUntilTransitionsFinished }
        ) {
            val mainViewModel: MainViewModel = hiltViewModel()
            val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

            MainScreen(
                onAddReminderButtonClick = { navController.navigate(Screen.AddReminderScreen.route) },
                onEditReminderClick = {
                    navController.navigate(
                        Screen.EditReminderScreen.createRoute(it)
                    )
                },
                onDeleteReminderClick = { mainViewModel.deleteReminder(it) },
                onBackDaySelectorButtonClick = { mainViewModel.moveDateRangeBack() },
                onForwardDaySelectorButtonClick = { mainViewModel.moveDateRangeForward() },
                selectableDays = uiState.selectableDays,
                remindersUiState = uiState.remindersUiState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            )
        }

        composable(
            route = Screen.AddReminderScreen.route,
            enterTransition = {
                if (initialState.destination.route == Screen.MainScreen.route) {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                } else {
                    EnterTransition.None
                }
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
            }
        ) {
            val addReminderViewModel: AddReminderViewModel = hiltViewModel()
            val uiState by addReminderViewModel.uiState.collectAsStateWithLifecycle()

            AddReminderScreen(
                onCancelButtonClick = { navController.navigateUp() },
                onConfirmButtonClick = {
                    if (addReminderViewModel.isReminderValid()) {
                        addReminderViewModel.addReminder()
                        navController.navigateUp()
                    }
                },
                title = uiState.reminderTitle,
                onTitleChange = { addReminderViewModel.onReminderTitleChange(it) },
                description = uiState.reminderDescription,
                onDescriptionChange = { addReminderViewModel.onReminderDescriptionChange(it) },
                dateTime = uiState.reminderDateTime,
                onTimeChange = { hour, minute ->
                    addReminderViewModel.onReminderTimeChange(
                        hour,
                        minute
                    )
                },
                onDateChange = { addReminderViewModel.onReminderDateChange(it) },
                isReminderValid = uiState.isReminderValid,
                repeat = uiState.reminderRepeat,
                onRepeatChange = { addReminderViewModel.onReminderRepeatChange(it) },
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            )
        }

        composable(
            route = Screen.EditReminderScreen.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType }),
            enterTransition = {
                if (initialState.destination.route == Screen.MainScreen.route) {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                } else {
                    EnterTransition.None
                }
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
            }
        ) { backStackEntry ->
            val reminderId = backStackEntry.arguments?.getInt("id") ?: -1

            val editReminderViewModel: EditReminderViewModel = hiltViewModel()
            val uiState by editReminderViewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) { editReminderViewModel.loadReminder(reminderId) }

            EditReminderScreen(
                onCancelButtonClick = { navController.navigateUp() },
                onConfirmButtonClick = {
                    if (editReminderViewModel.isReminderValid()) {
                        editReminderViewModel.saveReminder()
                        navController.navigateUp()
                    }
                },
                title = uiState.reminderTitle,
                onTitleChange = { editReminderViewModel.onReminderTitleChange(it) },
                description = uiState.reminderDescription,
                onDescriptionChange = { editReminderViewModel.onReminderDescriptionChange(it) },
                dateTime = uiState.reminderDateTime,
                onTimeChange = { hour, minute ->
                    editReminderViewModel.onReminderTimeChange(
                        hour,
                        minute
                    )
                },
                onDateChange = { editReminderViewModel.onReminderDateChange(it) },
                isReminderValid = uiState.isReminderValid,
                repeat = uiState.reminderRepeat,
                onRepeatChange = { editReminderViewModel.onReminderRepeatChange(it) },
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            )
        }
    }
}
