package com.cabovianco.remindme.presentation.navigation

sealed class Screen(
    val route: String
) {
    data object WelcomeScreen : Screen("welcome")
    data object PermissionScreen : Screen("permission")
    data object MainScreen : Screen("main")
    data object AddReminderScreen : Screen("add_reminder")
    data object EditReminderScreen : Screen("edit_reminder/{id}") {
        fun createRoute(id: Int) = "edit_reminder/$id"
    }
}
