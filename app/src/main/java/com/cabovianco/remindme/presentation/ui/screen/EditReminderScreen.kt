package com.cabovianco.remindme.presentation.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.cabovianco.remindme.R
import com.cabovianco.remindme.presentation.viewmodel.EditReminderViewModel

@Composable
fun EditReminderScreen(
    reminderId: Long,
    onBackClick: () -> Unit,
    onCreateTag: () -> Unit,
    viewModel: EditReminderViewModel,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(reminderId) {
        viewModel.loadReminder(reminderId)
    }

    ReminderFormScreen(
        title = stringResource(R.string.editor_title_edit),
        viewModel = viewModel,
        onBackClick = onBackClick,
        onSaveClick = {
            viewModel.saveReminder()
            onBackClick()
        },
        onCreateTag = onCreateTag,
        modifier = modifier
    )
}
