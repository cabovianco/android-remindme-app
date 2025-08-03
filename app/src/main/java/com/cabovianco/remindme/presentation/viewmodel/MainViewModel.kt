package com.cabovianco.remindme.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.model.Repeat
import com.cabovianco.remindme.domain.usecase.DeleteReminderUseCase
import com.cabovianco.remindme.domain.usecase.GetAllRemindersWithinDateRangeUseCase
import com.cabovianco.remindme.presentation.state.MainUiState
import com.cabovianco.remindme.presentation.state.RemindersUiState
import com.cabovianco.remindme.service.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAllRemindersWithinDateRangeUseCase: GetAllRemindersWithinDateRangeUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {
    private val from = ZonedDateTime.now()
        .withHour(0)
        .withMinute(0)
        .withSecond(0)
        .withNano(0)

    private val dateRange = MutableStateFlow(
        from to from.plusDays(4)
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = dateRange
        .map { (from, to) ->
            getSelectableDays()
        }
        .flatMapLatest { selectableDays ->
            val (from, to) = dateRange.value
            getAllRemindersWithinDateRangeUseCase(from, to)
                .map { reminders ->
                    MainUiState(
                        selectableDays = selectableDays,
                        remindersUiState = RemindersUiState.Success(getReminders(reminders))
                    )
                }
                .catch { emit(MainUiState(selectableDays, RemindersUiState.Error)) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainUiState()
        )

    private fun getReminders(reminders: List<Reminder>): List<Reminder> {
        val result = mutableListOf<Reminder>()
        reminders.forEach { reminder ->
            result.add(reminder)
            if (reminder.repeat != Repeat.Never) {
                var nextDate = reminder.repeat.nextDate(reminder.dateTime)
                while (nextDate <= dateRange.value.second) {
                    result.add(reminder.copy(dateTime = nextDate))
                    nextDate = reminder.repeat.nextDate(nextDate)
                }
            }
        }

        return result
    }

    private fun updateDateRange(newFrom: ZonedDateTime, newTo: ZonedDateTime) {
        dateRange.value = newFrom to newTo
    }

    fun moveDateRangeBack() {
        val (from, to) = dateRange.value
        updateDateRange(from.minusDays(5), to.minusDays(5))
    }

    fun moveDateRangeForward() {
        val (from, to) = dateRange.value
        updateDateRange(from.plusDays(5), to.plusDays(5))
    }

    private fun getSelectableDays(): List<ZonedDateTime> {
        val from = dateRange.value.first
        val to = dateRange.value.second
        return generateSequence(from) { it.plusDays(1) }
            .takeWhile { !it.isAfter(to) }
            .toList()
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            alarmScheduler.cancel(reminder.id)
            deleteReminderUseCase(reminder)
        }
    }
}
