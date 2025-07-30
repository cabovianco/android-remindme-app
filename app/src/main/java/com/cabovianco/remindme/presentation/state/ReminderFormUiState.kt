package com.cabovianco.remindme.presentation.state

import com.cabovianco.remindme.domain.model.Repeat
import java.time.ZonedDateTime

data class ReminderFormUiState(
    val reminderId: Int = 0,
    val reminderTitle: String = "",
    val reminderDescription: String? = null,
    val reminderDateTime: ZonedDateTime = ZonedDateTime.now(),
    val reminderRepeat: Repeat = Repeat.Never,
    val isReminderValid: Boolean = false
)
