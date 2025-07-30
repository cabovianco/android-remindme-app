package com.cabovianco.remindme.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.cabovianco.remindme.domain.usecase.InsertReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddReminderViewModel @Inject constructor(
    private val insertReminderUseCase: InsertReminderUseCase
) : ReminderFormViewModel() {
    fun addReminder(): Boolean {
        val reminder = createReminder()
        if (reminder == null) {
            return false
        }

        viewModelScope.launch {
            insertReminderUseCase(reminder)
        }

        return true
    }
}
