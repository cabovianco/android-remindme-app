package com.cabovianco.remindme.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.model.ReminderPriority
import com.cabovianco.remindme.domain.model.ReminderRepeat
import com.cabovianco.remindme.domain.model.Tag
import com.cabovianco.remindme.domain.usecase.AdjustReminderRepeatUseCase
import com.cabovianco.remindme.domain.usecase.GetAllTagsUseCase
import com.cabovianco.remindme.domain.usecase.ReplaceDateKeepingTimeUseCase
import com.cabovianco.remindme.presentation.state.ReminderFormUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek

open class ReminderFormViewModel(
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val adjustReminderRepeatUseCase: AdjustReminderRepeatUseCase,
    private val replaceDateKeepingTimeUseCase: ReplaceDateKeepingTimeUseCase
) : ViewModel() {
    protected val mutableUiState: MutableStateFlow<ReminderFormUiState> =
        MutableStateFlow(ReminderFormUiState())
    val uiState get() = mutableUiState.asStateFlow()

    init {
        loadTags()
    }

    private fun loadTags() {
        getAllTagsUseCase()
            .catch { emit(emptyList()) }
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
        mutableUiState.update {
            it.copy(
                dateTime = replaceDateKeepingTimeUseCase(it.dateTime, selectedDateMillis)
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
            it.copy(
                repeat = adjustReminderRepeatUseCase(
                    it.repeat,
                    repeat,
                    it.dateTime
                )
            )
        }
    }

    fun onReminderRepeatDayToggle(day: DayOfWeek) {
        mutableUiState.update {
            val weekly = it.repeat as? ReminderRepeat.Weekly ?: return@update it

            val days = if (day in weekly.days) weekly.days - day else weekly.days + day

            it.copy(repeat = weekly.copy(days = days))
        }
    }

    fun onReminderPriorityChange(priority: ReminderPriority?) {
        mutableUiState.update {
            val newPriority = if (it.priority == priority) null else priority
            it.copy(priority = newPriority)
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
            priority = priority,
            tags = selectedTags.toList()
        )
    }
}
