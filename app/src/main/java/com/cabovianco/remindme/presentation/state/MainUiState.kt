package com.cabovianco.remindme.presentation.state

import com.cabovianco.remindme.domain.model.Reminder
import java.time.ZonedDateTime

data class MainUiState(
    val selectedDate: ZonedDateTime = ZonedDateTime.now(),
    val selectableDates: List<ZonedDateTime> = emptyList(),
    val mainState: MainState = MainState.Loading
)

sealed interface MainState {
    data class Success(val reminders: List<Reminder> = emptyList()) : MainState
    data object Loading : MainState
    data object Error : MainState
}
