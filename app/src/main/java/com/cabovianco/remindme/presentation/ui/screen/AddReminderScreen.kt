package com.cabovianco.remindme.presentation.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.cabovianco.remindme.R
import com.cabovianco.remindme.presentation.viewmodel.AddReminderViewModel

@Composable
fun AddReminderScreen(
    onBackClick: () -> Unit,
    onCreateTag: () -> Unit,
    viewModel: AddReminderViewModel,
    modifier: Modifier = Modifier
) {
    ReminderFormScreen(
        modifier = modifier,
        title = stringResource(R.string.reminder_create_title),
        viewModel = viewModel,
        onBackClick = onBackClick,
        onSaveClick = {
            viewModel.addReminder()
            onBackClick()
        },
        onCreateTag = onCreateTag
    )
}
