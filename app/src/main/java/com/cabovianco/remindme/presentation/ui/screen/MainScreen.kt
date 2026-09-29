package com.cabovianco.remindme.presentation.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cabovianco.remindme.R
import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.model.ReminderPriority
import com.cabovianco.remindme.domain.model.ReminderRepeat
import com.cabovianco.remindme.domain.model.Tag
import com.cabovianco.remindme.presentation.state.MainState
import com.cabovianco.remindme.presentation.ui.screen.shared.AppBottomSheet
import com.cabovianco.remindme.presentation.ui.screen.shared.AppButton
import com.cabovianco.remindme.presentation.ui.screen.shared.AppIconButton
import com.cabovianco.remindme.presentation.ui.screen.shared.ButtonVariant
import com.cabovianco.remindme.presentation.ui.screen.shared.CompactTagChip
import com.cabovianco.remindme.presentation.ui.screen.shared.CreateTagButton
import com.cabovianco.remindme.presentation.ui.screen.shared.DashedDivider
import com.cabovianco.remindme.presentation.ui.screen.shared.EmptyState
import com.cabovianco.remindme.presentation.ui.screen.shared.ErrorState
import com.cabovianco.remindme.presentation.ui.screen.shared.HorizontalSelector
import com.cabovianco.remindme.presentation.ui.screen.shared.LoadingState
import com.cabovianco.remindme.presentation.ui.screen.shared.PriorityChip
import com.cabovianco.remindme.presentation.ui.screen.shared.TagChip
import com.cabovianco.remindme.presentation.ui.screen.shared.toColor
import com.cabovianco.remindme.presentation.ui.util.capitalizeFirst
import com.cabovianco.remindme.presentation.viewmodel.MainViewModel
import java.time.Month
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MainScreen(
    onAddReminder: () -> Unit,
    onEditReminder: (Long) -> Unit,
    onCreateTag: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedDate = uiState.selectedDate

    var showFilterSheet by remember { mutableStateOf(value = false) }
    var tagToDelete by remember { mutableStateOf<Tag?>(null) }
    var reminderToDelete by remember { mutableStateOf<Reminder?>(null) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar(
                month = selectedDate.month,
                year = selectedDate.year,
                onPreviousRange = { viewModel.moveDateRangeBack() },
                onNextRange = { viewModel.moveDateRangeForward() }
            )
        },
        floatingActionButton = {
            AddReminderFloatingButton(onClick = onAddReminder)
        }
    ) { padding ->
        MainContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            selectedDate = selectedDate,
            onSelectedDateChange = { viewModel.onSelectedDateChange(it) },
            selectableDates = uiState.selectableDates,
            selectedTags = uiState.selectedTags,
            selectedPriority = uiState.selectedPriority,
            onFilterClick = { showFilterSheet = true },
            onSettingsClick = onSettingsClick,
            onEditReminder = onEditReminder,
            onDeleteReminder = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                reminderToDelete = it
            },
            uiState = uiState.mainState
        )
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            tags = uiState.tags,
            selectedTags = uiState.selectedTags,
            selectedPriority = uiState.selectedPriority,
            onDismiss = { showFilterSheet = false },
            onApply = { tags, priority ->
                viewModel.setFilters(tags, priority)
                showFilterSheet = false
            },
            onTagLongClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                tagToDelete = it
            },
            onAddTagClick = onCreateTag
        )
    }

    tagToDelete?.let { tag ->
        DeleteConfirmationBottomSheet(
            title = stringResource(R.string.tag_delete_title),
            message = stringResource(R.string.tag_delete_message, tag.name),
            onDismiss = { tagToDelete = null },
            onConfirm = {
                viewModel.deleteTag(tag)
                tagToDelete = null
            }
        )
    }

    reminderToDelete?.let { reminder ->
        DeleteConfirmationBottomSheet(
            title = stringResource(R.string.reminder_delete_title),
            message = stringResource(R.string.reminder_delete_message, reminder.title),
            onDismiss = { reminderToDelete = null },
            onConfirm = {
                viewModel.deleteReminder(reminder)
                reminderToDelete = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    month: Month,
    year: Int,
    onPreviousRange: () -> Unit,
    onNextRange: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = "${
                    month.getDisplayName(TextStyle.FULL, Locale.getDefault()).capitalizeFirst()
                }, $year",
                style = MaterialTheme.typography.headlineMedium
            )
        },
        actions = {
            IconButton(onClick = onPreviousRange) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = null
                )
            }

            IconButton(onClick = onNextRange) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_forward),
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
private fun AddReminderFloatingButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    FloatingActionButton(
        modifier = modifier,
        onClick = onClick,
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_add),
            contentDescription = null
        )
    }
}

@Composable
private fun MainContent(
    selectedDate: ZonedDateTime,
    onSelectedDateChange: (ZonedDateTime) -> Unit,
    selectableDates: List<ZonedDateTime>,
    selectedTags: Set<Tag>,
    selectedPriority: ReminderPriority?,
    onFilterClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onEditReminder: (Long) -> Unit,
    onDeleteReminder: (Reminder) -> Unit,
    uiState: MainState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DateRangePicker(
            modifier = Modifier.fillMaxWidth(),
            selectedDate = selectedDate,
            onSelectedDate = onSelectedDateChange,
            selectableDates = selectableDates,
            isLoading = uiState is MainState.Loading
        )

        FilterHeader(
            modifier = Modifier.fillMaxWidth(),
            activeFiltersCount = selectedTags.size + (if (selectedPriority != null) 1 else 0),
            onFilterClick = onFilterClick,
            onSettingsClick = onSettingsClick
        )

        MainStateContent(
            modifier = Modifier.fillMaxSize(),
            uiState = uiState,
            onEditReminder = onEditReminder,
            onDeleteReminder = onDeleteReminder
        )
    }
}

@Composable
private fun FilterHeader(
    activeFiltersCount: Int,
    onFilterClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterButton(
            activeFiltersCount = activeFiltersCount,
            onClick = onFilterClick
        )

        SettingsButton(
            onClick = onSettingsClick
        )
    }
}

@Composable
private fun MainStateContent(
    uiState: MainState,
    onEditReminder: (Long) -> Unit,
    onDeleteReminder: (Reminder) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when (uiState) {
            is MainState.Success -> {
                ReminderList(
                    modifier = Modifier.fillMaxSize(),
                    reminders = uiState.reminders,
                    onEditReminder = onEditReminder,
                    onDeleteReminder = onDeleteReminder
                )
            }

            is MainState.Loading -> LoadingState(
                modifier = Modifier.fillMaxSize()
            )

            is MainState.Error -> ErrorState(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun DateRangePicker(
    selectedDate: ZonedDateTime,
    onSelectedDate: (ZonedDateTime) -> Unit,
    selectableDates: List<ZonedDateTime>,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .height(80.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )

        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                selectableDates.forEach { date ->
                    DateItem(
                        modifier = Modifier.weight(1f),
                        date = date,
                        isSelected = selectedDate.toLocalDate() == date.toLocalDate(),
                        onClick = { onSelectedDate(date) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DateItem(
    date: ZonedDateTime,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isToday = date.toLocalDate() == ZonedDateTime.now().toLocalDate()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick,
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        ) {
            DateItemContent(
                modifier = Modifier.fillMaxWidth(),
                date = date
            )
        }

        Box(
            modifier = Modifier.height(6.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isToday) {
                TodayIndicator()
            }
        }
    }
}

@Composable
private fun DateItemContent(
    date: ZonedDateTime,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.alpha(0.6f),
            text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                .capitalizeFirst(),
            style = MaterialTheme.typography.labelMedium
        )

        Text(
            text = date.dayOfMonth.toString(),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
private fun TodayIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(4.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
    )
}

@Composable
private fun FilterButton(
    activeFiltersCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BadgedBox(
        modifier = modifier,
        badge = {
            if (activeFiltersCount > 0) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(text = activeFiltersCount.toString())
                }
            }
        }
    ) {
        AppIconButton(
            onClick = onClick,
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_filter),
                    contentDescription = null
                )
            }
        )
    }
}

@Composable
private fun SettingsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppIconButton(
        modifier = modifier,
        onClick = onClick,
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_settings),
                contentDescription = null
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBottomSheet(
    tags: List<Tag>,
    selectedTags: Set<Tag>,
    selectedPriority: ReminderPriority?,
    onDismiss: () -> Unit,
    onApply: (Set<Tag>, ReminderPriority?) -> Unit,
    onTagLongClick: (Tag) -> Unit,
    onAddTagClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var localSelectedTags by remember { mutableStateOf(selectedTags) }
    var localSelectedPriority by remember { mutableStateOf(selectedPriority) }

    AppBottomSheet(
        modifier = modifier,
        onDismiss = onDismiss,
        content = {
            FilterBottomSheetContent(
                tags = tags,
                localSelectedTags = localSelectedTags,
                localSelectedPriority = localSelectedPriority,
                onTagClick = { tag ->
                    localSelectedTags = if (tag in localSelectedTags) localSelectedTags - tag
                    else localSelectedTags + tag
                },
                onPriorityClick = { priority ->
                    localSelectedPriority = if (localSelectedPriority == priority) null
                    else priority
                },
                onTagLongClick = onTagLongClick,
                onAddTagClick = onAddTagClick
            )
        },
        actions = {
            FilterBottomSheetActions(
                onApply = {
                    val validTags = localSelectedTags.filter { localTag ->
                        tags.any { it.id == localTag.id }
                    }.toSet()
                    onApply(validTags, localSelectedPriority)
                },
                onClear = {
                    localSelectedTags = emptySet()
                    localSelectedPriority = null
                }
            )
        }
    )
}

@Composable
private fun FilterBottomSheetContent(
    tags: List<Tag>,
    localSelectedTags: Set<Tag>,
    localSelectedPriority: ReminderPriority?,
    onTagClick: (Tag) -> Unit,
    onPriorityClick: (ReminderPriority) -> Unit,
    onTagLongClick: (Tag) -> Unit,
    onAddTagClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        PriorityFilterSection(
            selectedPriority = localSelectedPriority,
            onPriorityClick = onPriorityClick
        )

        TagFilterSection(
            tags = tags,
            selectedTags = localSelectedTags,
            onTagClick = onTagClick,
            onTagLongClick = onTagLongClick,
            onAddTagClick = onAddTagClick
        )
    }
}

@Composable
private fun PriorityFilterSection(
    selectedPriority: ReminderPriority?,
    onPriorityClick: (ReminderPriority) -> Unit,
    modifier: Modifier = Modifier
) {
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
        options = ReminderPriority.entries
    ) { priority ->
        PriorityChip(
            priority = priority,
            isSelected = priority == selectedPriority,
            onClick = { onPriorityClick(priority) }
        )
    }
}

@Composable
private fun TagFilterSection(
    tags: List<Tag>,
    selectedTags: Set<Tag>,
    onTagClick: (Tag) -> Unit,
    onTagLongClick: (Tag) -> Unit,
    onAddTagClick: () -> Unit,
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
        afterContent = { CreateTagButton(onClick = onAddTagClick) }
    ) { tag ->
        TagChip(
            text = tag.name,
            isSelected = tag in selectedTags,
            onClick = { onTagClick(tag) },
            onLongClick = { onTagLongClick(tag) },
            icon = tag.icon,
            color = tag.color
        )
    }
}

@Composable
private fun FilterBottomSheetActions(
    onApply: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AppButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.filter_action_apply),
            onClick = onApply,
            variant = ButtonVariant.Primary
        )

        AppButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.filter_action_clear),
            onClick = onClear,
            variant = ButtonVariant.Secondary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteConfirmationBottomSheet(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppBottomSheet(
        modifier = modifier,
        onDismiss = onDismiss,
        title = title,
        icon = {
            Icon(
                modifier = Modifier.size(80.dp),
                painter = painterResource(R.drawable.ic_delete),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        content = {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(0.7f),
                text = message,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        actions = {
            DeleteConfirmationActions(
                modifier = Modifier.fillMaxWidth(),
                onConfirm = onConfirm,
                onDismiss = onDismiss
            )
        }
    )
}

@Composable
private fun DeleteConfirmationActions(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AppButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.action_delete),
            onClick = onConfirm,
            variant = ButtonVariant.Danger
        )

        AppButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.action_cancel),
            onClick = onDismiss,
            variant = ButtonVariant.Secondary
        )
    }
}

@Composable
private fun ReminderList(
    reminders: List<Reminder>,
    onEditReminder: (Long) -> Unit,
    onDeleteReminder: (Reminder) -> Unit,
    modifier: Modifier = Modifier
) {
    when (reminders) {
        emptyList<Reminder>() -> EmptyState(modifier = modifier)

        else -> ReminderListContent(
            modifier = modifier,
            reminders = reminders,
            onEditReminder = onEditReminder,
            onDeleteReminder = onDeleteReminder
        )
    }
}

@Composable
private fun ReminderListContent(
    reminders: List<Reminder>,
    onEditReminder: (Long) -> Unit,
    onDeleteReminder: (Reminder) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(reminders, key = { it.id }) { reminder ->
            ReminderEntry(
                reminder = reminder,
                onEdit = { onEditReminder(reminder.id) },
                onDelete = { onDeleteReminder(reminder) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ReminderEntry(
    reminder: Reminder,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = onEdit,
                onLongClick = onDelete
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.5f))
    ) {
        ReminderEntryContent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            title = reminder.title,
            description = reminder.description,
            date = reminder.dateTime.format(DateTimeFormatter.ofPattern("HH:mm")),
            tags = reminder.tags,
            repeat = reminder.repeat,
            priority = reminder.priority
        )
    }
}

@Composable
private fun ReminderEntryContent(
    title: String,
    description: String?,
    date: String,
    tags: List<Tag>,
    repeat: ReminderRepeat,
    priority: ReminderPriority?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = title,
            style = MaterialTheme.typography.titleMedium
        )

        description?.let { text ->
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        DashedDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )

        ReminderMetadata(
            modifier = Modifier.fillMaxWidth(),
            time = date,
            tags = tags,
            repeat = repeat,
            priority = priority
        )
    }
}

@Composable
private fun ReminderDate(
    date: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = date,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.labelSmall
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReminderMetadata(
    time: String,
    tags: List<Tag>,
    repeat: ReminderRepeat,
    priority: ReminderPriority?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f, fill = false),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ReminderTimeChip(time = time)
            ReminderRepeatChip(repeat = repeat)
        }

        Row(
            modifier = Modifier.padding(start = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            priority?.let {
                ReminderPriorityIcon(priority = it)
            }

            ReminderTagsInfo(tags = tags)
        }
    }
}

@Composable
private fun ReminderTimeChip(
    time: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        ReminderDate(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            date = time
        )
    }
}

@Composable
private fun ReminderRepeatChip(
    repeat: ReminderRepeat,
    modifier: Modifier = Modifier
) {
    if (repeat != ReminderRepeat.Never) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(12.dp),
                    painter = painterResource(R.drawable.ic_repeat),
                    contentDescription = null
                )

                Text(
                    text = repeat.toShortString(),
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun ReminderPriorityIcon(
    priority: ReminderPriority,
    modifier: Modifier = Modifier
) {
    Icon(
        modifier = modifier.size(16.dp),
        painter = painterResource(R.drawable.ic_priority),
        contentDescription = null,
        tint = priority.toColor()
    )
}

@Composable
private fun ReminderTagsInfo(
    tags: List<Tag>,
    modifier: Modifier = Modifier
) {
    if (tags.isNotEmpty()) {
        val displayTags = tags.take(1)
        val remaining = tags.size - displayTags.size

        displayTags.forEach { tag ->
            Box(
                modifier = modifier,
                contentAlignment = Alignment.TopEnd
            ) {
                CompactTagChip(
                    modifier = Modifier.padding(end = if (remaining > 0) 12.dp else 0.dp),
                    text = tag.name,
                    icon = tag.icon,
                    color = tag.color
                )

                if (remaining > 0) {
                    Surface(
                        modifier = Modifier.size(18.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "+$remaining",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReminderRepeat.toShortString(): String = when (this) {
    ReminderRepeat.Never -> ""
    is ReminderRepeat.Daily -> stringResource(R.string.repeat_daily)
    is ReminderRepeat.Weekly -> {
        if (days.isEmpty()) stringResource(R.string.repeat_weekly)
        else if (days.size == 7) stringResource(R.string.repeat_daily)
        else if (days.size > 2) {
            "${days.size} ${stringResource(R.string.repeat_option_day).lowercase()}s"
        } else {
            days.sorted().joinToString(", ") {
                it.getDisplayName(TextStyle.SHORT, Locale.getDefault()).capitalizeFirst()
            }
        }
    }

    is ReminderRepeat.Monthly -> stringResource(R.string.repeat_monthly)
    is ReminderRepeat.Yearly -> stringResource(R.string.repeat_yearly)
}
