package com.cabovianco.remindme.presentation.ui.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cabovianco.remindme.R
import com.cabovianco.remindme.domain.model.Repeat
import com.cabovianco.remindme.domain.model.Repeat.Daily
import com.cabovianco.remindme.domain.model.Repeat.Monthly
import com.cabovianco.remindme.domain.model.Repeat.Never
import com.cabovianco.remindme.domain.model.Repeat.Weekly
import com.cabovianco.remindme.domain.model.Repeat.Yearly
import com.cabovianco.remindme.presentation.ui.screen.shared.LabeledCard
import com.cabovianco.remindme.presentation.ui.screen.shared.NavigationTopBar
import com.cabovianco.remindme.presentation.ui.screen.shared.PrimaryButton
import com.cabovianco.remindme.presentation.ui.screen.shared.PrimaryTextField
import com.cabovianco.remindme.presentation.ui.screen.shared.SelectableTag
import com.cabovianco.remindme.presentation.viewmodel.ReminderFormViewModel
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ReminderFormScreen(
    title: String,
    viewModel: ReminderFormViewModel,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = { NavigationTopBar(title = title, onBackClick = onBackClick) },
        bottomBar = { SaveButton(onClick = onSaveClick) }
    ) { padding ->
        ReminderFormContent(
            title = uiState.reminderTitle,
            onTitleChange = { viewModel.onReminderTitleChange(it) },
            description = uiState.reminderDescription,
            onDescriptionChange = { viewModel.onReminderDescriptionChange(it) },
            dateTime = uiState.reminderDateTime,
            onTimeChange = { hour, minute -> viewModel.onReminderTimeChange(hour, minute) },
            onDateChange = { viewModel.onReminderDateChange(it) },
            repeat = uiState.reminderRepeat,
            onRepeatChange = { viewModel.onReminderRepeatChange(it) },
            isReminderValid = uiState.isReminderValid,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun ReminderFormContent(
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
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        TitleDescriptionSection(
            title = title,
            onTitleChange = onTitleChange,
            description = description,
            onDescriptionChange = onDescriptionChange,
            isTitleError = !isReminderValid
        )

        DateTimeSection(
            dateTime = dateTime,
            onDateChange = onDateChange,
            onTimeChange = onTimeChange
        )

        OptionSelector(
            label = stringResource(R.string.editor_repeat_label),
            icon = {
                Icon(
                    painter = painterResource(R.drawable.repeat),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            options = listOf(Never, Daily, Weekly, Monthly, Yearly),
            selectedOption = repeat,
            onOptionSelected = onRepeatChange,
            optionName = { stringResource(it.toResId()) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun TitleDescriptionSection(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String?,
    onDescriptionChange: (String) -> Unit,
    isTitleError: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PrimaryTextField(
            modifier = Modifier.fillMaxWidth(),
            value = title,
            onValueChange = onTitleChange,
            label = stringResource(R.string.editor_title_hint),
            isError = isTitleError,
            singleLine = true
        )

        PrimaryTextField(
            modifier = Modifier.fillMaxWidth(),
            value = description ?: "",
            onValueChange = onDescriptionChange,
            label = stringResource(R.string.editor_desc_hint),
            maxLines = 5,
            minLines = 5
        )
    }
}

@Composable
private fun DateTimeSection(
    dateTime: ZonedDateTime,
    onDateChange: (Long) -> Unit,
    onTimeChange: (Int, Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DateField(
            selectedDateMillis = dateTime.toInstant().toEpochMilli(),
            onDateChange = onDateChange,
            modifier = Modifier.weight(1f)
        )

        TimeField(
            hour = dateTime.hour,
            minute = dateTime.minute,
            onTimeChange = onTimeChange,
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateField(
    selectedDateMillis: Long,
    onDateChange: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    val date = Instant.ofEpochMilli(selectedDateMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

    LabeledCard(
        modifier = modifier,
        title = stringResource(R.string.editor_date_label),
        icon = {
            Icon(
                painter = painterResource(R.drawable.calendar),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        value = date.format(DateTimeFormatter.ofPattern(stringResource(R.string.common_date_format))),
        onClick = { showDialog = true }
    )

    if (showDialog) {
        DatePickerDialog(
            selectedDateMillis = selectedDateMillis,
            onDismiss = { showDialog = false },
            onConfirm = {
                onDateChange(it)
                showDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    selectedDateMillis: Long,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDateMillis
    )

    val colors = DatePickerDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { datePickerState.selectedDateMillis?.let { onConfirm(it) } }
            ) {
                Text(text = stringResource(R.string.common_btn_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(android.R.string.cancel))
            }
        },
        shape = MaterialTheme.shapes.extraLarge,
        tonalElevation = 8.dp,
        colors = colors
    ) {
        DatePicker(
            state = datePickerState,
            showModeToggle = false,
            title = null,
            headline = null,
            colors = colors
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeField(
    hour: Int,
    minute: Int,
    onTimeChange: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    LabeledCard(
        modifier = modifier,
        title = stringResource(R.string.editor_time_label),
        icon = {
            Icon(
                painter = painterResource(R.drawable.time),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        value = LocalTime.of(hour, minute).format(DateTimeFormatter.ofPattern("HH:mm")),
        onClick = { showDialog = true }
    )

    if (showDialog) {
        TimePickerDialog(
            hour = hour,
            minute = minute,
            onDismiss = { showDialog = false },
            onConfirm = { h, m ->
                onTimeChange(h, m)
                showDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    hour: Int,
    minute: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = hour,
        initialMinute = minute,
        is24Hour = true
    )

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min),
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 8.dp,
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TimePicker(modifier = Modifier.padding(16.dp), state = timePickerState)
                TimePickerActions(
                    onDismiss = onDismiss,
                    onConfirm = { onConfirm(timePickerState.hour, timePickerState.minute) }
                )
            }
        }
    }
}

@Composable
private fun TimePickerActions(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 6.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onDismiss) {
            Text(text = stringResource(android.R.string.cancel))
        }

        TextButton(onClick = onConfirm) {
            Text(text = stringResource(R.string.common_btn_save))
        }
    }
}

@Composable
private fun <T> OptionSelector(
    label: String,
    icon: @Composable () -> Unit,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    optionName: @Composable (T) -> String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.size(20.dp)) { icon() }

            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        OptionCarousel(
            options = options,
            selectedOption = selectedOption,
            onOptionSelected = onOptionSelected,
            optionName = optionName,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun <T> OptionCarousel(
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    optionName: @Composable (T) -> String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selectedOption

            SelectableTag(
                text = optionName(option),
                isSelected = isSelected,
                onClick = { onOptionSelected(option) }
            )
        }
    }
}

@Composable
private fun SaveButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    PrimaryButton(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(24.dp),
        text = stringResource(R.string.common_btn_save),
        onClick = onClick
    )
}

private fun Repeat.toResId() = when (this) {
    Never -> R.string.repeat_option_never
    Daily -> R.string.repeat_option_daily
    Weekly -> R.string.repeat_option_weekly
    Monthly -> R.string.repeat_option_monthly
    Yearly -> R.string.repeat_option_yearly
}
