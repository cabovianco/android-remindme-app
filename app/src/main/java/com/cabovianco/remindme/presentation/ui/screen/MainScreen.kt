package com.cabovianco.remindme.presentation.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cabovianco.remindme.R
import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.model.ReminderRepeat
import com.cabovianco.remindme.domain.model.Tag
import com.cabovianco.remindme.presentation.state.MainState
import com.cabovianco.remindme.presentation.ui.screen.shared.DashedDivider
import com.cabovianco.remindme.presentation.ui.screen.shared.EmptyState
import com.cabovianco.remindme.presentation.ui.screen.shared.ErrorState
import com.cabovianco.remindme.presentation.ui.screen.shared.LoadingState
import com.cabovianco.remindme.presentation.ui.screen.shared.PrimaryButton
import com.cabovianco.remindme.presentation.ui.screen.shared.TagChip
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
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedDate = uiState.selectedDate
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
        floatingActionButton = { AddReminderFloatingButton(onClick = onAddReminder) }
    ) { padding ->
        MainContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            selectedDate = selectedDate,
            onSelectedDateChange = { viewModel.onSelectedDateChange(it) },
            selectableDates = uiState.selectableDates,
            tags = uiState.tags,
            selectedTags = uiState.selectedTags,
            onTagSelected = { viewModel.toggleTag(it) },
            onTagLongClick = { tagToDelete = it },
            onCreateTag = onCreateTag,
            onEditReminder = onEditReminder,
            onDeleteReminder = { reminderToDelete = it },
            uiState = uiState.mainState
        )
    }

    tagToDelete?.let { tag ->
        DeleteConfirmationBottomSheet(
            title = stringResource(R.string.main_delete_tag_title),
            message = stringResource(R.string.main_delete_tag_message, tag.name),
            onDismiss = { tagToDelete = null },
            onConfirm = {
                viewModel.deleteTag(tag)
                tagToDelete = null
            }
        )
    }

    reminderToDelete?.let { reminder ->
        DeleteConfirmationBottomSheet(
            title = stringResource(R.string.main_delete_reminder_title),
            message = stringResource(R.string.main_delete_reminder_message, reminder.title),
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
                    painter = painterResource(R.drawable.arrow_left),
                    contentDescription = null
                )
            }

            IconButton(onClick = onNextRange) {
                Icon(
                    painter = painterResource(R.drawable.arrow_right),
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
            painter = painterResource(R.drawable.add),
            contentDescription = null
        )
    }
}

@Composable
private fun MainContent(
    selectedDate: ZonedDateTime,
    onSelectedDateChange: (ZonedDateTime) -> Unit,
    selectableDates: List<ZonedDateTime>,
    tags: List<Tag>,
    selectedTags: Set<Tag>,
    onTagSelected: (Tag) -> Unit,
    onTagLongClick: (Tag) -> Unit,
    onCreateTag: () -> Unit,
    onEditReminder: (Long) -> Unit,
    onDeleteReminder: (Reminder) -> Unit,
    uiState: MainState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DateRangePicker(
            selectedDate = selectedDate,
            onSelectedDate = onSelectedDateChange,
            selectableDates = selectableDates,
            modifier = Modifier.fillMaxWidth()
        )

        TagCarousel(
            tags = tags,
            selectedTags = selectedTags,
            onTagSelected = onTagSelected,
            onTagLongClick = onTagLongClick,
            onAddTagClick = onCreateTag,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when (uiState) {
                is MainState.Success -> {
                    ReminderList(
                        reminders = uiState.reminders,
                        onEditReminder = onEditReminder,
                        onDeleteReminder = onDeleteReminder,
                        modifier = Modifier.fillMaxSize()
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
}

@Composable
private fun DateRangePicker(
    selectedDate: ZonedDateTime,
    onSelectedDate: (ZonedDateTime) -> Unit,
    selectableDates: List<ZonedDateTime>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        selectableDates.forEach { date ->
            DateItem(
                date = date,
                isSelected = selectedDate.toLocalDate() == date.toLocalDate(),
                onClick = { onSelectedDate(date) },
                modifier = Modifier.weight(1f)
            )
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        ) {
            DateItemContent(
                date = date,
                modifier = Modifier.fillMaxWidth()
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
            text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                .capitalizeFirst(),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.alpha(0.6f)
        )

        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
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
private fun TagCarousel(
    tags: List<Tag>,
    selectedTags: Set<Tag>,
    onTagSelected: (Tag) -> Unit,
    onTagLongClick: (Tag) -> Unit,
    onAddTagClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allTag = Tag(id = -1, name = stringResource(R.string.tag_all))

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item {
            TagChip(
                text = allTag.name,
                isSelected = selectedTags.isEmpty(),
                onClick = { onTagSelected(allTag) }
            )
        }

        items(tags) {
            TagChip(
                text = it.name,
                isSelected = it in selectedTags,
                onClick = { onTagSelected(it) },
                onLongClick = { onTagLongClick(it) },
                icon = it.icon,
                color = it.color
            )
        }

        item { CreateTagButton(onClick = onAddTagClick) }
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

@Composable
private fun ReminderList(
    reminders: List<Reminder>,
    onEditReminder: (Long) -> Unit,
    onDeleteReminder: (Reminder) -> Unit,
    modifier: Modifier = Modifier
) {
    when (reminders) {
        emptyList<Reminder>() -> EmptyState(
            text = stringResource(R.string.main_empty_state_label),
            painter = painterResource(R.drawable.empty_state),
            modifier = modifier
        )

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
            repeat = reminder.repeat
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
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )

        description?.let { text ->
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }

        DashedDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ReminderDate(date = date)

            ReminderMetadata(
                tags = tags,
                repeat = repeat
            )
        }
    }
}

@Composable
private fun ReminderDate(
    date: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = date,
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}

@Composable
private fun ReminderMetadata(
    tags: List<Tag>,
    repeat: ReminderRepeat,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (repeat != ReminderRepeat.Never) {
            ReminderMetadataItem(icon = R.drawable.repeat)
        }

        if (tags.isNotEmpty()) {
            val count = tags.size

            ReminderMetadataItem(
                icon = R.drawable.tag,
                text = if (count > 1) "+${count - 1}" else null
            )
        }
    }
}

@Composable
private fun ReminderMetadataItem(
    icon: Int,
    modifier: Modifier = Modifier,
    text: String? = null
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(14.dp),
            painter = painterResource(icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        text?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
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
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        DeleteConfirmationContent(
            title = title,
            message = message,
            onDismiss = onDismiss,
            onConfirm = onConfirm,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Composable
private fun DeleteConfirmationContent(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            modifier = Modifier
                .align(Alignment.End)
                .size(32.dp),
            onClick = onDismiss,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(R.drawable.cancel),
                contentDescription = null
            )
        }

        Icon(
            modifier = Modifier.size(80.dp),
            painter = painterResource(R.drawable.delete),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.alpha(0.7f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        DeleteConfirmationActions(
            onConfirm = onConfirm,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun DeleteConfirmationActions(
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    PrimaryButton(
        modifier = modifier,
        text = stringResource(R.string.common_btn_delete),
        onClick = onConfirm,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    )
}
