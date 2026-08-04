package com.cabovianco.remindme.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cabovianco.remindme.data.alarm.AlarmScheduler
import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.model.ReminderPriority
import com.cabovianco.remindme.domain.model.Tag
import com.cabovianco.remindme.domain.usecase.DeleteReminderUseCase
import com.cabovianco.remindme.domain.usecase.DeleteTagUseCase
import com.cabovianco.remindme.domain.usecase.FilterRemindersUseCase
import com.cabovianco.remindme.domain.usecase.GetAllRemindersUseCase
import com.cabovianco.remindme.domain.usecase.GetAllTagsUseCase
import com.cabovianco.remindme.domain.usecase.GetReminderOccurrencesUseCase
import com.cabovianco.remindme.domain.usecase.GetSelectableDatesUseCase
import com.cabovianco.remindme.presentation.state.MainState
import com.cabovianco.remindme.presentation.state.MainUiState
import com.cabovianco.remindme.presentation.state.ReminderFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAllRemindersUseCase: GetAllRemindersUseCase,
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase,
    private val deleteTagUseCase: DeleteTagUseCase,
    private val getReminderOccurrencesUseCase: GetReminderOccurrencesUseCase,
    private val getSelectableDatesUseCase: GetSelectableDatesUseCase,
    private val filterRemindersUseCase: FilterRemindersUseCase,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {
    private val today = ZonedDateTime.now()
        .withHour(0)
        .withMinute(0)
        .withSecond(0)
        .withNano(0)

    private val startOfCurrentWeek = today.minusDays(today.dayOfWeek.value.toLong() % 7)
    private val endOfCurrentWeek =
        startOfCurrentWeek
            .plusDays(6)
            .withHour(23)
            .withMinute(59)
            .withSecond(59)

    private val _dateRange = MutableStateFlow(startOfCurrentWeek to endOfCurrentWeek)

    private val _selectedDate = MutableStateFlow(today)

    private val _filters = MutableStateFlow(ReminderFilters())

    val uiState = combine(
        getAllRemindersUseCase(),
        getAllTagsUseCase(),
        _dateRange,
        _selectedDate,
        _filters
    ) { reminders, tags, range, selectedDate, filters ->
        val dayReminders = getReminderOccurrencesUseCase(reminders, selectedDate)
        val filteredReminders = filterRemindersUseCase(
            reminders = dayReminders,
            selectedTags = filters.tags,
            selectedPriority = filters.priority
        )

        MainUiState(
            selectedDate = selectedDate,
            selectableDates = getSelectableDatesUseCase(
                from = range.first,
                to = range.second
            ),
            tags = tags,
            selectedTags = filters.tags,
            selectedPriority = filters.priority,
            mainState = MainState.Success(filteredReminders)
        )
    }.catch {
        emit(MainUiState(mainState = MainState.Error))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState()
    )

    fun onSelectedDateChange(date: ZonedDateTime) {
        _selectedDate.value = date
    }

    fun moveDateRangeBack() = moveDateRange(-7)

    fun moveDateRangeForward() = moveDateRange(7)

    private fun moveDateRange(days: Long) {
        val currentRange = _dateRange.value
        _dateRange.value = currentRange.first.plusDays(days) to currentRange.second.plusDays(days)
        onSelectedDateChange(_selectedDate.value.plusDays(days))
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            alarmScheduler.cancel(reminder.id)
            deleteReminderUseCase(reminder)
        }
    }

    fun setFilters(tags: Set<Tag>, priority: ReminderPriority?) {
        _filters.value = ReminderFilters(tags, priority)
    }

    fun deleteTag(tag: Tag) {
        viewModelScope.launch {
            deleteTagUseCase(tag)
            _filters.update { current ->
                current.copy(tags = current.tags.filter { it.id != tag.id }.toSet())
            }
        }
    }
}
