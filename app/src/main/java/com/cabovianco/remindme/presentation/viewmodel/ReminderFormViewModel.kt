package com.cabovianco.remindme.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.model.ReminderRepeat
import com.cabovianco.remindme.domain.model.Tag
import com.cabovianco.remindme.domain.usecase.GetAllTagsUseCase
import com.cabovianco.remindme.presentation.state.ReminderFormUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId

open class ReminderFormViewModel(
    private val getAllTagsUseCase: GetAllTagsUseCase
) : ViewModel() {
    protected val mutableUiState: MutableStateFlow<ReminderFormUiState> =
        MutableStateFlow(ReminderFormUiState())
    val uiState get() = mutableUiState.asStateFlow()

    init {
        loadTags()
    }

    private fun loadTags() {
        getAllTagsUseCase()
            .onEach { tags ->
                mutableUiState.update { it.copy(tags = tags) }
            }
            .launchIn(viewModelScope)
    }

    fun onReminderTitleChange(title: String) {
        mutableUiState.update { it.copy(title = title) }
    }

    fun onReminderDescriptionChange(description: String) {
        mutableUiState.update { it.copy(description = description.ifBlank { null }) }
    }

    fun onReminderDateChange(selectedDateMillis: Long) {
        val date = Instant.ofEpochMilli(selectedDateMillis)
            .atZone(ZoneId.of("UTC"))
            .toLocalDate()

        mutableUiState.update {
            it.copy(
                dateTime = it.dateTime
                    .withYear(date.year)
                    .withMonth(date.monthValue)
                    .withDayOfMonth(date.dayOfMonth)
            )
        }
    }

    fun onReminderTimeChange(hour: Int, minute: Int) {
        mutableUiState.update {
            it.copy(
                dateTime = it.dateTime
                    .withHour(hour)
                    .withMinute(minute)
            )
        }
    }

    fun onReminderRepeatChange(repeat: ReminderRepeat) {
        mutableUiState.update { it.copy(repeat = repeat) }
    }

    fun onReminderRepeatIntervalChange(interval: Int) {
        mutableUiState.update {
            it.copy(repeat = it.repeat.copyWith(interval))
        }
    }

    fun onReminderRepeatFrequencyChange(repeat: ReminderRepeat) {
        mutableUiState.update {
            val currentInterval = it.repeat.interval
            var repeat = repeat.copyWith(currentInterval)

            if (repeat is ReminderRepeat.Weekly && repeat.days.isEmpty()) {
                val dayOfReminder = it.dateTime.dayOfWeek
                repeat = repeat.copy(days = setOf(dayOfReminder))
            }

            it.copy(repeat = repeat)
        }
    }

    fun onReminderRepeatDayToggle(day: DayOfWeek) {
        mutableUiState.update {
            val weekly = it.repeat as? ReminderRepeat.Weekly ?: return@update it

            val days = if (day in weekly.days) weekly.days - day else weekly.days + day

            it.copy(repeat = weekly.copy(days = days))
        }
    }

    fun onTagSelected(tag: Tag) {
        mutableUiState.update {
            val selected = it.selectedTags

            it.copy(selectedTags = if (tag in selected) selected - tag else selected + tag)
        }
    }

    fun createReminder() = with(mutableUiState.value) {
        Reminder(
            id = id,
            title = title,
            description = description,
            dateTime = dateTime,
            repeat = repeat,
            tags = selectedTags.toList()
        )
    }
}
