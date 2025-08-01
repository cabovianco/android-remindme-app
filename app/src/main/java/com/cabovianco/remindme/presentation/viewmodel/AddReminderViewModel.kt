package com.cabovianco.remindme.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.cabovianco.remindme.domain.usecase.InsertReminderUseCase
import com.cabovianco.remindme.service.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddReminderViewModel @Inject constructor(
    private val insertReminderUseCase: InsertReminderUseCase,
    private val alarmScheduler: AlarmScheduler
) : ReminderFormViewModel() {
    fun addReminder() {
        val reminder = createReminder()

        viewModelScope.launch {
            val result = insertReminderUseCase(reminder)
            val id = result.getOrElse {
                return@launch
            }.toInt()

            alarmScheduler.schedule(reminder.copy(id = id))
        }
    }
}
