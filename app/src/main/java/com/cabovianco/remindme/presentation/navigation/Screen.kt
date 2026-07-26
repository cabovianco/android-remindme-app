package com.cabovianco.remindme.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    object WelcomeScreen : Screen

    @Serializable
    object PermissionScreen : Screen

    @Serializable
    object MainScreen : Screen

    @Serializable
    object AddReminderScreen : Screen

    @Serializable
    data class EditReminderScreen(val id: Long) : Screen

    @Serializable
    object CreateTagScreen : Screen
}
