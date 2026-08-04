package com.cabovianco.remindme.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.cabovianco.remindme.data.alarm.AlarmScheduler
import com.cabovianco.remindme.domain.usecase.AdjustReminderRepeatUseCase
import com.cabovianco.remindme.domain.usecase.GetAllTagsUseCase
import com.cabovianco.remindme.domain.usecase.InsertReminderUseCase
import com.cabovianco.remindme.domain.usecase.ReplaceDateKeepingTimeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddReminderViewModel @Inject constructor(
    private val insertReminderUseCase: InsertReminderUseCase,
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val adjustReminderRepeatUseCase: AdjustReminderRepeatUseCase,
    private val replaceDateKeepingTimeUseCase: ReplaceDateKeepingTimeUseCase,
    private val alarmScheduler: AlarmScheduler
) : ReminderFormViewModel(getAllTagsUseCase, adjustReminderRepeatUseCase, replaceDateKeepingTimeUseCase) {
    fun addReminder() {
        val reminder = createReminder()

        viewModelScope.launch {
            val result = insertReminderUseCase(reminder)
            val id = result.getOrElse {
                return@launch
            }

            alarmScheduler.schedule(reminder.copy(id = id))
        }
    }
}
