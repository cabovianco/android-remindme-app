package com.cabovianco.remindme.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.cabovianco.remindme.data.alarm.AlarmScheduler
import com.cabovianco.remindme.domain.usecase.GetAllTagsUseCase
import com.cabovianco.remindme.domain.usecase.GetReminderByIdUseCase
import com.cabovianco.remindme.domain.usecase.UpdateReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditReminderViewModel @Inject constructor(
    private val getReminderByIdUseCase: GetReminderByIdUseCase,
    private val updateReminderUseCase: UpdateReminderUseCase,
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val alarmScheduler: AlarmScheduler
) : ReminderFormViewModel(getAllTagsUseCase) {
    fun loadReminder(id: Long) {
        viewModelScope.launch {
            getReminderByIdUseCase(id)
                .filterNotNull()
                .collect { reminder ->
                    mutableUiState.update {
                        with(reminder) {
                            it.copy(
                                id = id,
                                title = title,
                                description = description,
                                dateTime = dateTime,
                                repeat = repeat,
                                selectedTags = tags.toSet()
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
