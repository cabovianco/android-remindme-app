package com.cabovianco.remindme.presentation.ui.screen

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cabovianco.remindme.R
import com.cabovianco.remindme.domain.model.Repeat
import com.cabovianco.remindme.domain.model.Repeat.Daily
import com.cabovianco.remindme.domain.model.Repeat.Monthly
import com.cabovianco.remindme.domain.model.Repeat.Never
import com.cabovianco.remindme.domain.model.Repeat.Weekly
import com.cabovianco.remindme.domain.model.Repeat.Yearly
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ReminderFormScreen(
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
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(
            onCancelButtonClick = { onCancelButtonClick() },
            onConfirmButtonClick = { onConfirmButtonClick() },
            modifier = Modifier.fillMaxWidth()
        )

        ReminderTitle(
            title = title,
            onTitleChange = { onTitleChange(it) },
            isError = !isReminderValid,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
        )

        ReminderDescription(
            description = description,
            onDescriptionChange = { onDescriptionChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
        )

        ReminderTime(
            hour = dateTime.hour,
            minute = dateTime.minute,
            onTimeChange = { hour, minute -> onTimeChange(hour, minute) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
        )

        ReminderDate(
            selectedDateMillis = dateTime.toInstant().toEpochMilli(),
            onDateChange = { onDateChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp, start = 16.dp, end = 16.dp)
        )

        ReminderRepeat(
            repeat = repeat,
            onRepeatChange = { onRepeatChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp, start = 16.dp, end = 16.dp)
        )
    }
}

private fun hasExactAlarmPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.canScheduleExactAlarms()
    } else {
        true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    onCancelButtonClick: () -> Unit,
    onConfirmButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showPermissionDialog by rememberSaveable { mutableStateOf(false) }
    if (showPermissionDialog) {
        PermissionDialog(
            onDismiss = { showPermissionDialog = false },
            onConfirm = {
                showPermissionDialog = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    context.startActivity(intent)
                }
            }
        )
    }

    TopAppBar(
        modifier = modifier,
        title = {},
        actions = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedIconButton(
                    onClick = { onCancelButtonClick() }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.cancel),
                        contentDescription = null
                    )
                }

                FilledIconButton(
                    onClick = {
                        if (hasExactAlarmPermission(context)) {
                            onConfirmButtonClick()
                        } else {
                            showPermissionDialog = true
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.confirm),
                        contentDescription = null
                    )
                }
            }
        },
        windowInsets = WindowInsets(top = 0.dp),
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
private fun PermissionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.set_exact_alarm_permission_dialog_title)) },
        text = { Text(text = stringResource(R.string.set_exact_alarm_permission_dialog_description)) },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(text = stringResource(R.string.set_exact_alarm_permission_dialog_button))
            }
        }
    )
}

@Composable
private fun ReminderTitle(
    title: String,
    onTitleChange: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        modifier = modifier,
        value = title,
        onValueChange = { onTitleChange(it) },
        label = { Text(text = stringResource(R.string.title_label)) },
        isError = isError,
        singleLine = true,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun ReminderDescription(
    description: String?,
    onDescriptionChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        modifier = modifier,
        value = description ?: "",
        onValueChange = { onDescriptionChange(it) },
        label = { Text(text = stringResource(R.string.description_label)) },
        shape = RoundedCornerShape(16.dp),
        maxLines = 4,
        minLines = 4
    )
}

@Composable
private fun ReminderInput(
    inputIconResId: Int,
    inputLabelResId: Int,
    inputValue: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(inputIconResId),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(inputLabelResId),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Text(
                text = inputValue,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun AddReminderInput(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(
            onClick = { onClick() }
        ) { Text(text = stringResource(R.string.select_text_button)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderTime(
    hour: Int,
    minute: Int,
    onTimeChange: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSheet by rememberSaveable { mutableStateOf(false) }

    val time = LocalTime.of(hour, minute)
    val formattedTime = time.format(DateTimeFormatter.ofPattern("HH:mm"))

    ReminderInput(
        inputIconResId = R.drawable.time,
        inputLabelResId = R.string.time_label,
        inputValue = formattedTime,
        onClick = { showSheet = !showSheet },
        modifier = modifier
    )

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            val timePickerState = rememberTimePickerState(hour, minute)

            Column {
                TimePicker(modifier = Modifier.fillMaxWidth(), state = timePickerState)

                AddReminderInput(
                    onClick = {
                        onTimeChange(timePickerState.hour, timePickerState.minute)
                        showSheet = false
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderDate(
    selectedDateMillis: Long,
    onDateChange: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSheet by rememberSaveable { mutableStateOf(false) }

    val date = Instant.ofEpochMilli(selectedDateMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

    val formattedDate = date.format(
        DateTimeFormatter.ofPattern(stringResource(R.string.date_format))
    )

    ReminderInput(
        inputIconResId = R.drawable.calendar,
        inputLabelResId = R.string.calendar_label,
        inputValue = formattedDate,
        onClick = { showSheet = !showSheet },
        modifier = modifier
    )

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            val datePickerState =
                rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)

            Column {
                DatePicker(
                    modifier = Modifier.fillMaxWidth(),
                    state = datePickerState,
                    showModeToggle = false,
                    title = null,
                    headline = null,
                    colors = DatePickerDefaults.colors(containerColor = BottomSheetDefaults.ContainerColor)
                )

                AddReminderInput(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { onDateChange(it) }
                        showSheet = false
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}

private fun Repeat.toResId() = when (this) {
    Never -> R.string.repeat_never
    Daily -> R.string.repeat_daily
    Weekly -> R.string.repeat_weekly
    Monthly -> R.string.repeat_monthly
    Yearly -> R.string.repeat_yearly
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderRepeat(
    repeat: Repeat,
    onRepeatChange: (Repeat) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSheet by rememberSaveable { mutableStateOf(false) }

    val repeatOptions = listOf(Never, Daily, Weekly, Monthly, Yearly)

    ReminderInput(
        inputIconResId = R.drawable.repeat,
        inputLabelResId = R.string.repeat_label,
        inputValue = stringResource(repeat.toResId()),
        onClick = { showSheet = !showSheet },
        modifier = modifier
    )

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            var selectedRepeat by remember { mutableStateOf(repeat) }

            Column {
                repeatOptions.forEach {
                    Row(
                        modifier = Modifier.padding(bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedRepeat == it,
                            onClick = { selectedRepeat = it }
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(text = stringResource(it.toResId()))
                    }
                }

                AddReminderInput(
                    onClick = {
                        onRepeatChange(selectedRepeat)
                        showSheet = false
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}
