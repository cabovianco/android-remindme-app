package com.cabovianco.remindme.presentation.state

import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.model.ReminderHistoryEntry
import com.cabovianco.remindme.domain.model.ReminderPriority
import com.cabovianco.remindme.domain.model.Tag
import java.time.ZonedDateTime

data class MainUiState(
    val selectedDate: ZonedDateTime = ZonedDateTime.now(),
    val selectableDates: List<ZonedDateTime> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val selectedTags: Set<Tag> = emptySet(),
    val selectedPriority: ReminderPriority? = null,
    val mainState: MainState = MainState.Loading,
    val isHistoryEnabled: Boolean = true
)

sealed interface ReminderItem {
    val id: Long
    val time: ZonedDateTime
    val priority: ReminderPriority?

    data class Active(val reminder: Reminder) : ReminderItem {
        override val id: Long get() = reminder.id
        override val time: ZonedDateTime get() = reminder.dateTime
        override val priority: ReminderPriority? get() = reminder.priority
    }

    data class History(val entry: ReminderHistoryEntry) : ReminderItem {
        override val id: Long get() = entry.id
        override val time: ZonedDateTime get() = entry.triggeredAt
        override val priority: ReminderPriority? get() = entry.priority
    }
}

sealed interface MainState {
    data class Success(val items: List<ReminderItem> = emptyList()) : MainState
    data object Loading : MainState
    data object Error : MainState
}
