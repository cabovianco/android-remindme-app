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
import java.time.ZonedDateTime

open class ReminderFormViewModel : ViewModel() {
    protected val mutableUiState: MutableStateFlow<ReminderFormUiState> =
        MutableStateFlow(ReminderFormUiState())
    val uiState get() = mutableUiState.asStateFlow()

    fun onReminderTitleChange(title: String) {
        mutableUiState.update {
            it.copy(
                reminderTitle = title,
                isReminderValid = title.isNotBlank()
            )
        }
    }

    fun onReminderDescriptionChange(description: String) {
        mutableUiState.update { it.copy(reminderDescription = description) }
    }

    fun onReminderDateChange(selectedDateMillis: Long) {
        val utcDateTime = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(selectedDateMillis),
            ZoneId.of("UTC")
        )

        val localDateTime = utcDateTime.withZoneSameInstant(ZoneId.systemDefault())
            .withYear(utcDateTime.year)
            .withMonth(utcDateTime.monthValue)
            .withDayOfMonth(utcDateTime.dayOfMonth)
            .withHour(mutableUiState.value.reminderDateTime.hour)
            .withMinute(mutableUiState.value.reminderDateTime.minute)

        mutableUiState.update { it.copy(reminderDateTime = localDateTime) }
    }

    fun onReminderTimeChange(hour: Int, minute: Int) {
        mutableUiState.update {
            it.copy(
                reminderDateTime = it.reminderDateTime
                    .withHour(hour)
                    .withMinute(minute)
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

    fun createReminder() = with(mutableUiState.value) {
        Reminder(
            id = reminderId,
            title = reminderTitle,
            description = reminderDescription,
            dateTime = reminderDateTime,
            repeat = reminderRepeat
        )
    }
}
