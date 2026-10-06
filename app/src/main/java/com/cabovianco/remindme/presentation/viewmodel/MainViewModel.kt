package com.cabovianco.remindme.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cabovianco.remindme.data.alarm.AlarmScheduler
import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.model.ReminderHistoryEntry
import com.cabovianco.remindme.domain.model.ReminderPriority
import com.cabovianco.remindme.domain.model.Tag
import com.cabovianco.remindme.domain.usecase.DeleteReminderHistoryEntryUseCase
import com.cabovianco.remindme.domain.usecase.DeleteReminderUseCase
import com.cabovianco.remindme.domain.usecase.DeleteTagUseCase
import com.cabovianco.remindme.domain.usecase.FilterRemindersUseCase
import com.cabovianco.remindme.domain.usecase.GetAllRemindersUseCase
import com.cabovianco.remindme.domain.usecase.GetAllTagsUseCase
import com.cabovianco.remindme.domain.usecase.GetHistoryPreferencesUseCase
import com.cabovianco.remindme.domain.usecase.GetReminderHistoryUseCase
import com.cabovianco.remindme.domain.usecase.GetReminderOccurrencesUseCase
import com.cabovianco.remindme.domain.usecase.GetSelectableDatesUseCase
import com.cabovianco.remindme.presentation.state.MainState
import com.cabovianco.remindme.presentation.state.MainUiState
import com.cabovianco.remindme.presentation.state.ReminderItem
import com.cabovianco.remindme.presentation.ui.util.atEndOfDay
import com.cabovianco.remindme.presentation.ui.util.atEndOfWeek
import com.cabovianco.remindme.presentation.ui.util.atStartOfDay
import com.cabovianco.remindme.presentation.ui.util.atStartOfWeek
import com.cabovianco.remindme.presentation.ui.util.weekDatesForOffset
import com.cabovianco.remindme.presentation.ui.util.weekOffsetFrom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

private data class DataSnapshot(
    val reminders: List<Reminder>,
    val tags: List<Tag>,
    val history: List<ReminderHistoryEntry>,
    val isHistoryEnabled: Boolean
)

private data class FilterState(
    val tags: Set<Tag> = emptySet(),
    val priority: ReminderPriority? = null
)

private data class UiQueryState(
    val range: Pair<ZonedDateTime, ZonedDateTime>,
    val selectedDate: ZonedDateTime,
    val filterState: FilterState
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAllRemindersUseCase: GetAllRemindersUseCase,
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase,
    private val deleteTagUseCase: DeleteTagUseCase,
    private val deleteReminderHistoryEntryUseCase: DeleteReminderHistoryEntryUseCase,
    private val getReminderOccurrencesUseCase: GetReminderOccurrencesUseCase,
    private val getSelectableDatesUseCase: GetSelectableDatesUseCase,
    private val filterRemindersUseCase: FilterRemindersUseCase,
    private val getReminderHistoryUseCase: GetReminderHistoryUseCase,
    private val getHistoryPreferencesUseCase: GetHistoryPreferencesUseCase,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {
    private val today = ZonedDateTime.now().atStartOfDay()
    private val startOfCurrentWeek = today.atStartOfWeek()
    private val endOfCurrentWeek = today.atEndOfWeek()

    private val _dateRange = MutableStateFlow(startOfCurrentWeek to endOfCurrentWeek)
    private val _selectedDate = MutableStateFlow(today)
    private val _filterState = MutableStateFlow(FilterState())

    val uiState = combine(
        getAllRemindersUseCase(),
        getAllTagsUseCase(),
        getReminderHistoryUseCase(),
        getHistoryPreferencesUseCase()
            .map { it.isHistoryEnabled }
    ) { reminders, tags, history, isHistoryEnabled ->
        DataSnapshot(
            reminders = reminders,
            tags = tags,
            history = history,
            isHistoryEnabled = isHistoryEnabled
        )
    }.combine(
        combine(
            _dateRange,
            _selectedDate,
            _filterState
        ) { range, selectedDate, filters ->
            UiQueryState(
                range = range,
                selectedDate = selectedDate,
                filterState = filters
            )
        }
    ) { dataSnapshot, uiQueryState ->
        val selectedDate = uiQueryState.selectedDate
        val dayReminders = getReminderOccurrencesUseCase(dataSnapshot.reminders, selectedDate)
        val filteredReminders = filterRemindersUseCase(
            items = dayReminders,
            selectedTags = uiQueryState.filterState.tags,
            selectedPriority = uiQueryState.filterState.priority
        )

        val startOfDay = selectedDate.atStartOfDay()
        val endOfDay = selectedDate.atEndOfDay()

        val dayHistory = if (dataSnapshot.isHistoryEnabled) dataSnapshot.history.filter { entry ->
            !entry.triggeredAt.isBefore(startOfDay) && !entry.triggeredAt.isAfter(endOfDay)
        } else emptyList()

        val filteredHistory = filterRemindersUseCase(
            items = dayHistory,
            selectedTags = uiQueryState.filterState.tags,
            selectedPriority = uiQueryState.filterState.priority
        )

        val activeItems = filteredReminders.map { ReminderItem.Active(it) }
        val historyItems = filteredHistory.map { ReminderItem.History(it) }
        val allItems = (activeItems + historyItems).sortedBy { it.time }

        MainUiState(
            selectedDate = selectedDate,
            selectableDates = getSelectableDatesUseCase(
                from = uiQueryState.range.first,
                to = uiQueryState.range.second
            ),
            tags = dataSnapshot.tags,
            selectedTags = uiQueryState.filterState.tags,
            selectedPriority = uiQueryState.filterState.priority,
            mainState = MainState.Success(allItems),
            isHistoryEnabled = dataSnapshot.isHistoryEnabled
        )
    }.catch {
        emit(MainUiState(mainState = MainState.Error))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState()
    )

    companion object {
        const val INITIAL_PAGE = 52
        const val PAGE_COUNT = 105
    }

    fun getWeekDatesForPage(page: Int): List<ZonedDateTime> {
        val weekOffset = page - INITIAL_PAGE
        return startOfCurrentWeek.weekDatesForOffset(weekOffset)
    }

    fun getWeekPageIndex(date: ZonedDateTime): Int {
        val weekOffset = date.weekOffsetFrom(startOfCurrentWeek)
        return INITIAL_PAGE + weekOffset
    }

    fun onSelectedDateChange(date: ZonedDateTime) {
        _selectedDate.value = date

        val startOfWeek = date.atStartOfWeek()
        val endOfWeek = startOfWeek.atEndOfWeek()

        if (_dateRange.value != startOfWeek to endOfWeek) {
            _dateRange.value = startOfWeek to endOfWeek
        }
    }

    fun setFilters(tags: Set<Tag>, priority: ReminderPriority?) {
        _filterState.value = FilterState(tags, priority)
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            alarmScheduler.cancel(reminder.id)
            deleteReminderUseCase(reminder)
        }
    }

    fun deleteHistoryEntry(entry: ReminderHistoryEntry) {
        viewModelScope.launch {
            deleteReminderHistoryEntryUseCase(entry)
        }
    }

    fun deleteTag(tag: Tag) {
        viewModelScope.launch {
            deleteTagUseCase(tag)
            _filterState.update { current ->
                current.copy(tags = current.tags.filter { it.id != tag.id }.toSet())
            }
        }
    }
}
