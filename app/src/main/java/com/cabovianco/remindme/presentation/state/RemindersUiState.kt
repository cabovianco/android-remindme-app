package com.cabovianco.remindme.presentation.state

import com.cabovianco.remindme.domain.model.Reminder
import java.time.ZonedDateTime

sealed interface RemindersUiState {
    data class Success(val reminders: List<Reminder> = emptyList()) : RemindersUiState
    data object Loading : RemindersUiState
    data object Error : RemindersUiState
}

data class MainUiState(
    val selectableDays: List<ZonedDateTime> = emptyList(),
    val remindersUiState: RemindersUiState = RemindersUiState.Loading
)
