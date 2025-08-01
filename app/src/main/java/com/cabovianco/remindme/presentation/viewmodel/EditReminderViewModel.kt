package com.cabovianco.remindme.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.cabovianco.remindme.domain.usecase.GetReminderByIdUseCase
import com.cabovianco.remindme.domain.usecase.UpdateReminderUseCase
import com.cabovianco.remindme.service.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditReminderViewModel @Inject constructor(
    private val getReminderByIdUseCase: GetReminderByIdUseCase,
    private val updateReminderUseCase: UpdateReminderUseCase,
    private val alarmScheduler: AlarmScheduler
) : ReminderFormViewModel() {
    fun loadReminder(id: Int) {
        viewModelScope.launch {
            getReminderByIdUseCase(id)
                .filterNotNull()
                .collect { reminder ->
                    mutableUiState.update {
                        with(reminder) {
                            it.copy(
                                reminderId = id,
                                reminderTitle = title,
                                reminderDescription = description,
                                reminderDateTime = dateTime,
                                reminderRepeat = repeat
                            )
                        }
                    }
                }
        }
    }

    fun saveReminder() {
        val reminder = createReminder()

        viewModelScope.launch {
            val result = updateReminderUseCase(reminder)
            if (result.isFailure) {
                return@launch
            }

            alarmScheduler.schedule(reminder)
        }
    }
}
