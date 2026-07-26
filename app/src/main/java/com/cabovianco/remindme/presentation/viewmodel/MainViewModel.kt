package com.cabovianco.remindme.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cabovianco.remindme.data.alarm.AlarmScheduler
import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.model.ReminderRepeat
import com.cabovianco.remindme.domain.model.Tag
import com.cabovianco.remindme.domain.usecase.DeleteReminderUseCase
import com.cabovianco.remindme.domain.usecase.DeleteTagUseCase
import com.cabovianco.remindme.domain.usecase.GetAllRemindersUseCase
import com.cabovianco.remindme.domain.usecase.GetAllTagsUseCase
import com.cabovianco.remindme.presentation.state.MainState
import com.cabovianco.remindme.presentation.state.MainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAllRemindersUseCase: GetAllRemindersUseCase,
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase,
    private val deleteTagUseCase: DeleteTagUseCase,
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

    private val _selectedTags = MutableStateFlow(emptySet<Tag>())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState =
        combine(
            _dateRange,
            _selectedDate,
            _selectedTags,
            getAllTagsUseCase()
        ) { range, selectedDate, selectedTags, tags ->
            Data(range, selectedDate, selectedTags, tags)
        }.flatMapLatest { data ->
            getAllRemindersUseCase()
                .map { reminders ->
                    val dayReminders = dayOccurrences(reminders, data.selectedDate)
                    val filteredReminders = dayReminders.filter { reminder ->
                        data.selectedTags.isEmpty() ||
                                data.selectedTags.all { selectedTag ->
                                    reminder.tags.any { it.id == selectedTag.id }
                                }
                    }

                    MainUiState(
                        selectedDate = data.selectedDate,
                        selectableDates = datesInRange(from = data.range.first, to = data.range.second),
                        tags = data.tags,
                        selectedTags = data.selectedTags,
                        mainState = MainState.Success(filteredReminders)
                    )
                }
                .catch { emit(MainUiState(mainState = MainState.Error)) }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainUiState()
        )

    private data class Data(
        val range: Pair<ZonedDateTime, ZonedDateTime>,
        val selectedDate: ZonedDateTime,
        val selectedTags: Set<Tag>,
        val tags: List<Tag>
    )

    private fun datesInRange(from: ZonedDateTime, to: ZonedDateTime): List<ZonedDateTime> {
        return generateSequence(from) { it.plusDays(1) }
            .takeWhile { !it.isAfter(to) }
            .toList()
    }

    private fun dayOccurrences(
        reminders: List<Reminder>,
        date: ZonedDateTime
    ): List<Reminder> {
        val occurrences = mutableListOf<Reminder>()

        val startOfDay = date.withHour(0).withMinute(0).withSecond(0).withNano(0)
        val endOfDay = startOfDay.plusDays(1).minusNanos(1)

        reminders.forEach {
            var occurrenceDate = it.dateTime

            while (occurrenceDate.isBefore(startOfDay) && it.repeat != ReminderRepeat.Never) {
                occurrenceDate = it.repeat.next(occurrenceDate)
            }

            if (!occurrenceDate.isBefore(startOfDay) && !occurrenceDate.isAfter(endOfDay)) {
                occurrences.add(it.copy(dateTime = occurrenceDate))
            }
        }

        return occurrences.sortedBy { it.dateTime }
    }

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

    fun toggleTag(tag: Tag) {
        if (tag.id == -1L) {
            _selectedTags.value = emptySet()
            return
        }

        val current = _selectedTags.value
        _selectedTags.value = if (tag in current) current - tag else current + tag
    }

    fun deleteTag(tag: Tag) {
        viewModelScope.launch {
            deleteTagUseCase(tag)
        }
    }
}
