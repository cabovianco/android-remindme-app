package com.cabovianco.remindme.presentation.navigation

import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cabovianco.remindme.presentation.ui.screen.AddReminderScreen
import com.cabovianco.remindme.presentation.ui.screen.MainScreen
import com.cabovianco.remindme.presentation.ui.screen.PermissionScreen
import com.cabovianco.remindme.presentation.ui.screen.WelcomeScreen
import com.cabovianco.remindme.presentation.viewmodel.AddReminderViewModel
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
            if (navToPermissionScreen) {
                PermissionScreen(
                    onAccept = { navController.navigate(Screen.MainScreen.route) },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        composable(route = Screen.MainScreen.route) {
            val mainViewModel: MainViewModel = hiltViewModel()
            val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

            MainScreen(
                onAddReminderButtonClick = { navController.navigate(Screen.AddReminderScreen.route) },
                onEditReminderClick = { navController.navigate(Screen.EditReminderScreen.route) },
                onDeleteReminderClick = {mainViewModel.deleteReminder(it)},
                onBackDaySelectorButtonClick = { mainViewModel.moveDateRangeBack() },
                onForwardDaySelectorButtonClick = { mainViewModel.moveDateRangeForward() },
                selectableDays = uiState.selectableDays,
                remindersUiState = uiState.remindersUiState,
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(route = Screen.AddReminderScreen.route) {
            val addReminderViewModel: AddReminderViewModel = hiltViewModel()
            val uiState by addReminderViewModel.uiState.collectAsState()

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
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(route = Screen.EditReminderScreen.route) {

        }
    }
}
