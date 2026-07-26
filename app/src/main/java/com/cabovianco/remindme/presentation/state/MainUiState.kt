package com.cabovianco.remindme.presentation.state

import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.model.Tag
import java.time.ZonedDateTime

data class MainUiState(
    val selectedDate: ZonedDateTime = ZonedDateTime.now(),
    val selectableDates: List<ZonedDateTime> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val selectedTags: Set<Tag> = emptySet(),
    val mainState: MainState = MainState.Loading
)

sealed interface MainState {
    data class Success(val reminders: List<Reminder> = emptyList()) : MainState
    data object Loading : MainState
    data object Error : MainState
}
