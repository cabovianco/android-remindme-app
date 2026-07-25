package com.cabovianco.remindme.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cabovianco.remindme.data.alarm.AlarmScheduler
import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.model.Repeat
import com.cabovianco.remindme.domain.usecase.DeleteReminderUseCase
import com.cabovianco.remindme.domain.usecase.GetAllRemindersUseCase
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
    private val deleteReminderUseCase: DeleteReminderUseCase,
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState =
        combine(_dateRange, _selectedDate) { range, selected ->
            range to selected
        }.flatMapLatest { (range, selected) ->
            getAllRemindersUseCase()
                .map {
                    MainUiState(
                        selectedDate = selected,
                        selectableDates = datesInRange(from = range.first, to = range.second),
                        mainState = MainState.Success(
                            dayOccurrences(it, selected)
                        )
                    )
                }
                .catch { emit(MainUiState(mainState = MainState.Error)) }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainUiState()
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

            while (occurrenceDate.isBefore(startOfDay) && it.repeat != Repeat.Never) {
                occurrenceDate = it.repeat.nextDate(occurrenceDate)
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
}
