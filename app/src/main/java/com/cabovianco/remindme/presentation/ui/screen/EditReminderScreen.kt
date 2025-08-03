package com.cabovianco.remindme.presentation.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cabovianco.remindme.domain.model.Repeat
import java.time.ZonedDateTime

@Composable
fun EditReminderScreen(
    onCancelButtonClick: () -> Unit,
    onConfirmButtonClick: () -> Unit,
    title: String,
    onTitleChange: (String) -> Unit,
    description: String?,
    onDescriptionChange: (String) -> Unit,
    dateTime: ZonedDateTime,
    onTimeChange: (Int, Int) -> Unit,
    onDateChange: (Long) -> Unit,
    repeat: Repeat,
    onRepeatChange: (Repeat) -> Unit,
    isReminderValid: Boolean,
    modifier: Modifier = Modifier
) {
    ReminderFormScreen(
        onCancelButtonClick = onCancelButtonClick,
        onConfirmButtonClick = onConfirmButtonClick,
        title = title,
        onTitleChange = onTitleChange,
        description = description,
        onDescriptionChange = onDescriptionChange,
        dateTime = dateTime,
        onTimeChange = onTimeChange,
        onDateChange = onDateChange,
        repeat = repeat,
        onRepeatChange = onRepeatChange,
        isReminderValid = isReminderValid,
        modifier = modifier
    )
}
