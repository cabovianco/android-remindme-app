package com.cabovianco.remindme.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.model.Repeat
import com.cabovianco.remindme.presentation.state.ReminderFormUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.time.ZoneId

open class ReminderFormViewModel : ViewModel() {
    protected val mutableUiState: MutableStateFlow<ReminderFormUiState> =
        MutableStateFlow(ReminderFormUiState())
    val uiState get() = mutableUiState.asStateFlow()

    fun onReminderTitleChange(title: String) {
        mutableUiState.update { it.copy(reminderTitle = title) }
    }

    fun onReminderDescriptionChange(description: String) {
        mutableUiState.update { it.copy(reminderDescription = description) }
    }

    fun onReminderDateChange(selectedDateMillis: Long?) {
        if (selectedDateMillis == null) {
            return
        }

        val localDateTime = Instant.ofEpochMilli(selectedDateMillis)
            .atZone(ZoneId.systemDefault())

        mutableUiState.update { it.copy(reminderDateTime = localDateTime) }
    }

    fun onReminderTimeChange(hour: Int?, minute: Int?) {
        mutableUiState.update {
            it.copy(
                reminderDateTime = it.reminderDateTime
                    .withHour(hour ?: 0)
                    .withMinute(minute ?: 0)
            )
        }
    }

    fun onReminderRepeatChange(repeat: Repeat) {
        mutableUiState.update { it.copy(reminderRepeat = repeat) }
    }

    fun isReminderValid(): Boolean {
        val isValid = mutableUiState.value.reminderTitle.isNotBlank()
        mutableUiState.update { it.copy(isReminderValid = isValid) }

        return isValid
    }

    fun createReminder(): Reminder? {
        if (!mutableUiState.value.isReminderValid) {
            return null
        }

        return with(mutableUiState.value) {
            Reminder(
                id = reminderId,
                title = reminderTitle,
                description = reminderDescription,
                dateTime = reminderDateTime,
                repeat = reminderRepeat
            )
        }
    }
}
