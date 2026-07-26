package com.cabovianco.remindme.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cabovianco.remindme.R
import com.cabovianco.remindme.domain.model.ReminderRepeat
import com.cabovianco.remindme.domain.model.Tag
import com.cabovianco.remindme.presentation.ui.screen.shared.BottomActionButton
import com.cabovianco.remindme.presentation.ui.screen.shared.HorizontalSelector
import com.cabovianco.remindme.presentation.ui.screen.shared.InputHeader
import com.cabovianco.remindme.presentation.ui.screen.shared.NavigationTopBar
import com.cabovianco.remindme.presentation.ui.screen.shared.SelectionCard
import com.cabovianco.remindme.presentation.ui.screen.shared.TagChip
import com.cabovianco.remindme.presentation.ui.screen.shared.form.NumberStepper
import com.cabovianco.remindme.presentation.ui.screen.shared.form.PrimaryTextField
import com.cabovianco.remindme.presentation.viewmodel.ReminderFormViewModel
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ReminderFormScreen(
    title: String,
    viewModel: ReminderFormViewModel,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCreateTag: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = { NavigationTopBar(title = title, onBackClick = onBackClick) },
        bottomBar = {
            BottomActionButton(
                text = stringResource(R.string.common_btn_save),
                enabled = uiState.isValid,
                onClick = onSaveClick
            )
        }
    ) { padding ->
        ReminderFormContent(
            title = uiState.title,
            onTitleChange = { viewModel.onReminderTitleChange(it) },
            description = uiState.description,
            onDescriptionChange = { viewModel.onReminderDescriptionChange(it) },
            dateTime = uiState.dateTime,
            onTimeChange = { hour, minute -> viewModel.onReminderTimeChange(hour, minute) },
            onDateChange = { viewModel.onReminderDateChange(it) },
            repeat = uiState.repeat,
            onRepeatEnabledChange = {
                val repeat = if (it) ReminderRepeat.Weekly(
                    interval = 1,
                    days = setOf(uiState.dateTime.dayOfWeek)
                ) else ReminderRepeat.Never

                viewModel.onReminderRepeatChange(repeat)
            },
            onRepeatIntervalChange = { viewModel.onReminderRepeatIntervalChange(it) },
            onRepeatFrequencyChange = { viewModel.onReminderRepeatFrequencyChange(it) },
            onRepeatDayToggle = { viewModel.onReminderRepeatDayToggle(it) },
            tags = uiState.tags,
            selectedTags = uiState.selectedTags,
            onTagSelected = { viewModel.onTagSelected(it) },
            onCreateTag = onCreateTag,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun ReminderFormContent(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String?,
    onDescriptionChange: (String) -> Unit,
    dateTime: ZonedDateTime,
    onTimeChange: (Int, Int) -> Unit,
    onDateChange: (Long) -> Unit,
    repeat: ReminderRepeat,
    onRepeatEnabledChange: (Boolean) -> Unit,
    onRepeatIntervalChange: (Int) -> Unit,
    onRepeatFrequencyChange: (ReminderRepeat) -> Unit,
    onRepeatDayToggle: (DayOfWeek) -> Unit,
    tags: List<Tag>,
    selectedTags: Set<Tag>,
    onTagSelected: (Tag) -> Unit,
    onCreateTag: () -> Unit,
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
            modifier = Modifier.fillMaxWidth()
        )

        ScheduleSection(
            dateTime = dateTime,
            onDateChange = onDateChange,
            onTimeChange = onTimeChange,
            repeat = repeat,
            onRepeatEnabledChange = onRepeatEnabledChange,
            onRepeatIntervalChange = onRepeatIntervalChange,
            onRepeatFrequencyChange = onRepeatFrequencyChange,
            onRepeatDayToggle = onRepeatDayToggle,
            modifier = Modifier.fillMaxWidth()
        )

        TagSelectionSection(
            tags = tags,
            selectedTags = selectedTags,
            onTagSelected = onTagSelected,
            onCreateTagClick = onCreateTag,
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PrimaryTextField(
            modifier = Modifier.fillMaxWidth(),
            value = title,
            onValueChange = onTitleChange,
            label = stringResource(R.string.editor_title_hint),
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
private fun ScheduleSection(
    dateTime: ZonedDateTime,
    onDateChange: (Long) -> Unit,
    onTimeChange: (Int, Int) -> Unit,
    repeat: ReminderRepeat,
    onRepeatEnabledChange: (Boolean) -> Unit,
    onRepeatIntervalChange: (Int) -> Unit,
    onRepeatFrequencyChange: (ReminderRepeat) -> Unit,
    onRepeatDayToggle: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DateField(
                selectedDateMillis = dateTime.toLocalDate()
                    .atStartOfDay(ZoneId.of("UTC"))
                    .toInstant()
                    .toEpochMilli(),
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

        RepeatSection(
            repeat = repeat,
            onEnabledChange = onRepeatEnabledChange,
            onIntervalChange = onRepeatIntervalChange,
            onFrequencyChange = onRepeatFrequencyChange,
            onDayToggle = onRepeatDayToggle,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun DateField(
    selectedDateMillis: Long,
    onDateChange: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    val date = Instant.ofEpochMilli(selectedDateMillis)
        .atZone(ZoneId.of("UTC"))
        .toLocalDate()

    SelectionCard(
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
    onConfirm: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDateMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                utcTimeMillis >= ZonedDateTime.now(ZoneId.systemDefault())
                    .toLocalDate()
                    .atStartOfDay(ZoneId.of("UTC"))
                    .toInstant()
                    .toEpochMilli()
        }
    )

    val colors = DatePickerDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    )

    DatePickerDialog(
        modifier = modifier,
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

@Composable
private fun TimeField(
    hour: Int,
    minute: Int,
    onTimeChange: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    SelectionCard(
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
    onConfirm: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val timePickerState = rememberTimePickerState(
        initialHour = hour,
        initialMinute = minute,
        is24Hour = true
    )

    BasicAlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss
    ) {
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
private fun RepeatSection(
    repeat: ReminderRepeat,
    onEnabledChange: (Boolean) -> Unit,
    onIntervalChange: (Int) -> Unit,
    onFrequencyChange: (ReminderRepeat) -> Unit,
    onDayToggle: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier
) {
    val isEnabled = repeat !is ReminderRepeat.Never
    val interval = repeat.interval

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InputHeader(
                    text = stringResource(R.string.editor_repeat_label),
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.repeat),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )

                Box(
                    modifier = Modifier.height(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = onEnabledChange
                    )
                }
            }

            if (isEnabled) {
                RepeatOptions(
                    interval = interval,
                    onIntervalChange = onIntervalChange,
                    currentRepeat = repeat,
                    onFrequencyChange = onFrequencyChange,
                    onDayToggle = onDayToggle,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun RepeatOptions(
    interval: Int,
    onIntervalChange: (Int) -> Unit,
    currentRepeat: ReminderRepeat,
    onFrequencyChange: (ReminderRepeat) -> Unit,
    onDayToggle: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedDays =
        if (currentRepeat is ReminderRepeat.Weekly) currentRepeat.days else emptySet()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.repeat_every_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumberStepper(
                    value = interval,
                    onValueChange = onIntervalChange
                )

                FrequencyMenuSelector(
                    currentRepeat = currentRepeat,
                    onFrequencyChange = onFrequencyChange
                )
            }
        }

        if (currentRepeat is ReminderRepeat.Weekly) {
            val days = remember {
                listOf(DayOfWeek.SUNDAY) + DayOfWeek.entries.filter { it != DayOfWeek.SUNDAY }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                days.forEach { day ->
                    DayCircle(
                        text = day.getDisplayName(
                            java.time.format.TextStyle.NARROW,
                            Locale.getDefault()
                        ),
                        isSelected = day in selectedDays,
                        onClick = { onDayToggle(day) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FrequencyMenuSelector(
    currentRepeat: ReminderRepeat,
    onFrequencyChange: (ReminderRepeat) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(
        ReminderRepeat.Daily(),
        ReminderRepeat.Weekly(),
        ReminderRepeat.Monthly(),
        ReminderRepeat.Yearly()
    )

    Box(modifier = modifier) {
        Surface(
            onClick = { expanded = true },
            modifier = Modifier.height(36.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(currentRepeat.toLabelRes()),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    text = {
                        Text(
                            stringResource(option.toLabelRes()),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        onFrequencyChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun ReminderRepeat.toLabelRes(): Int = when (this) {
    is ReminderRepeat.Daily -> R.string.repeat_option_day
    is ReminderRepeat.Weekly -> R.string.repeat_option_week
    is ReminderRepeat.Monthly -> R.string.repeat_option_month
    is ReminderRepeat.Yearly -> R.string.repeat_option_year
    else -> R.string.repeat_option_day
}

@Composable
private fun DayCircle(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.size(38.dp),
        onClick = onClick,
        shape = CircleShape,
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TagSelectionSection(
    tags: List<Tag>,
    selectedTags: Set<Tag>,
    onTagSelected: (Tag) -> Unit,
    onCreateTagClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    HorizontalSelector(
        modifier = modifier,
        label = stringResource(R.string.editor_tag_label),
        icon = {
            Icon(
                painter = painterResource(R.drawable.tag),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        options = tags,
        afterContent = { CreateTagButton(onClick = onCreateTagClick) }
    ) { tag ->
        TagChip(
            text = tag.name,
            isSelected = tag in selectedTags,
            onClick = { onTagSelected(tag) },
            icon = tag.icon,
            color = tag.color
        )
    }
}

@Composable
private fun CreateTagButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        modifier = modifier.size(32.dp),
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Icon(
            modifier = Modifier.size(22.dp),
            painter = painterResource(R.drawable.add),
            contentDescription = null
        )
    }
}
