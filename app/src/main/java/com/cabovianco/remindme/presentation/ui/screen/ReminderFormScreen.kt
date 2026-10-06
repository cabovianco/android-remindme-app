package com.cabovianco.remindme.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cabovianco.remindme.R
import com.cabovianco.remindme.domain.model.ReminderPriority
import com.cabovianco.remindme.domain.model.ReminderRepeat
import com.cabovianco.remindme.domain.model.Tag
import com.cabovianco.remindme.presentation.ui.screen.shared.AppBottomSheet
import com.cabovianco.remindme.presentation.ui.screen.shared.AppButton
import com.cabovianco.remindme.presentation.ui.screen.shared.CreateTagButton
import com.cabovianco.remindme.presentation.ui.screen.shared.HorizontalSelector
import com.cabovianco.remindme.presentation.ui.screen.shared.InputHeader
import com.cabovianco.remindme.presentation.ui.screen.shared.NavigationTopBar
import com.cabovianco.remindme.presentation.ui.screen.shared.PriorityChip
import com.cabovianco.remindme.presentation.ui.screen.shared.SelectionCard
import com.cabovianco.remindme.presentation.ui.screen.shared.TagChip
import com.cabovianco.remindme.presentation.ui.screen.shared.form.AppTextField
import com.cabovianco.remindme.presentation.ui.screen.shared.form.NumberStepper
import com.cabovianco.remindme.presentation.viewmodel.ReminderFormViewModel
import java.time.DayOfWeek
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
    onCreateTag: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = { NavigationTopBar(title = title, onBackClick = onBackClick) },
        bottomBar = {
            AppButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(16.dp),
                text = stringResource(R.string.action_save),
                onClick = onSaveClick,
                enabled = uiState.isValid
            )
        }
    ) { padding ->
        ReminderFormContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
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
            priority = uiState.priority,
            onPriorityChange = { viewModel.onReminderPriorityChange(it) },
            tags = uiState.tags,
            selectedTags = uiState.selectedTags,
            onTagSelected = { viewModel.onTagSelected(it) },
            onCreateTag = onCreateTag
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
    priority: ReminderPriority?,
    onPriorityChange: (ReminderPriority?) -> Unit,
    tags: List<Tag>,
    selectedTags: Set<Tag>,
    onTagSelected: (Tag) -> Unit,
    onCreateTag: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TitleDescriptionSection(
            modifier = Modifier.fillMaxWidth(),
            title = title,
            onTitleChange = onTitleChange,
            description = description,
            onDescriptionChange = onDescriptionChange
        )

        ScheduleSection(
            modifier = Modifier.fillMaxWidth(),
            dateTime = dateTime,
            onDateChange = onDateChange,
            onTimeChange = onTimeChange,
            repeat = repeat,
            onRepeatEnabledChange = onRepeatEnabledChange,
            onRepeatIntervalChange = onRepeatIntervalChange,
            onRepeatFrequencyChange = onRepeatFrequencyChange,
            onRepeatDayToggle = onRepeatDayToggle
        )

        PrioritySelectionSection(
            modifier = Modifier.fillMaxWidth(),
            selectedPriority = priority,
            onPrioritySelected = onPriorityChange
        )

        TagSelectionSection(
            modifier = Modifier.fillMaxWidth(),
            tags = tags,
            selectedTags = selectedTags,
            onTagSelected = onTagSelected,
            onCreateTagClick = onCreateTag
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
        AppTextField(
            modifier = Modifier.fillMaxWidth(),
            value = title,
            onValueChange = onTitleChange,
            label = stringResource(R.string.reminder_title_hint),
            singleLine = true
        )

        AppTextField(
            modifier = Modifier.fillMaxWidth(),
            value = description ?: "",
            onValueChange = onDescriptionChange,
            label = stringResource(R.string.reminder_description_hint),
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
                modifier = Modifier.weight(1f),
                selectedDateMillis = dateTime.toLocalDate()
                    .atStartOfDay(ZoneId.of("UTC"))
                    .toInstant()
                    .toEpochMilli(),
                onDateChange = onDateChange
            )

            TimeField(
                modifier = Modifier.weight(1f),
                hour = dateTime.hour,
                minute = dateTime.minute,
                onTimeChange = onTimeChange
            )
        }

        RepeatSection(
            modifier = Modifier.fillMaxWidth(),
            repeat = repeat,
            onEnabledChange = onRepeatEnabledChange,
            onIntervalChange = onRepeatIntervalChange,
            onFrequencyChange = onRepeatFrequencyChange,
            onDayToggle = onRepeatDayToggle
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
        title = stringResource(R.string.reminder_date_label),
        value = date.format(DateTimeFormatter.ofPattern(stringResource(R.string.date_format))),
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_calendar),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        onClick = { showDialog = true }
    )

    if (showDialog) {
        DatePickerBottomSheet(
            selectedDateMillis = selectedDateMillis,
            onConfirm = {
                onDateChange(it)
                showDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerBottomSheet(
    selectedDateMillis: Long,
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

    AppBottomSheet(
        modifier = modifier,
        onDismiss = { datePickerState.selectedDateMillis?.let { onConfirm(it) } },
        content = {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                title = null,
                headline = null,
                showModeToggle = false
            )
        }
    )
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
        title = stringResource(R.string.reminder_time_label),
        value = LocalTime.of(hour, minute).format(DateTimeFormatter.ofPattern("HH:mm")),
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_schedule),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        onClick = { showDialog = true }
    )

    if (showDialog) {
        TimePickerBottomSheet(
            hour = hour,
            minute = minute,
            onConfirm = { hour, minute ->
                onTimeChange(hour, minute)
                showDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerBottomSheet(
    hour: Int,
    minute: Int,
    onConfirm: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val timePickerState = rememberTimePickerState(
        initialHour = hour,
        initialMinute = minute,
        is24Hour = true
    )

    AppBottomSheet(
        modifier = modifier,
        onDismiss = { onConfirm(timePickerState.hour, timePickerState.minute) },
        content = {
            TimePicker(
                modifier = Modifier.fillMaxWidth(),
                state = timePickerState
            )
        }
    )
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                InputHeader(
                    text = stringResource(R.string.reminder_repeat_label),
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_repeat),
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
                        onCheckedChange = onEnabledChange,
                        colors = SwitchDefaults.colors(
                            uncheckedTrackColor = Color.Transparent,
                            uncheckedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            if (isEnabled) {
                RepeatOptions(
                    modifier = Modifier.padding(top = 8.dp),
                    interval = interval,
                    onIntervalChange = onIntervalChange,
                    currentRepeat = repeat,
                    onFrequencyChange = onFrequencyChange,
                    onDayToggle = onDayToggle
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
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.repeat_every_label),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
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
                            LocalLocale.current.platformLocale
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
            modifier = Modifier.height(36.dp),
            onClick = { expanded = true },
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(currentRepeat.toLabelRes()),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(option.toLabelRes()),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        onFrequencyChange(option)
                        expanded = false
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
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
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun PrioritySelectionSection(
    selectedPriority: ReminderPriority?,
    onPrioritySelected: (ReminderPriority) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = ReminderPriority.entries

    HorizontalSelector(
        modifier = modifier,
        label = stringResource(R.string.reminder_priority_label),
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_priority),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        options = options
    ) { priority ->
        PriorityChip(
            priority = priority,
            isSelected = priority == selectedPriority,
            onClick = { onPrioritySelected(priority) }
        )
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
        label = stringResource(R.string.reminder_tags_label),
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_tag),
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
