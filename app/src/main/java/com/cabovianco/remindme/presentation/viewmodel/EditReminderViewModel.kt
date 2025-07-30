package com.cabovianco.remindme.presentation.viewmodel

import androidx.lifecycle.viewModelScope
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
    private val updateReminderUseCase: UpdateReminderUseCase
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

    fun saveReminder(): Boolean {
        val reminder = createReminder()
        if (reminder == null) {
            return false
        }

        viewModelScope.launch {
            updateReminderUseCase(reminder)
        }

        return true
    }
}
