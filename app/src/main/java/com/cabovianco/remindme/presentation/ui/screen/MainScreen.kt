package com.cabovianco.remindme.presentation.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cabovianco.remindme.R
import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.presentation.state.MainState
import com.cabovianco.remindme.presentation.ui.screen.shared.DashedDivider
import com.cabovianco.remindme.presentation.ui.screen.shared.EmptyStateContent
import com.cabovianco.remindme.presentation.ui.screen.shared.ErrorContent
import com.cabovianco.remindme.presentation.ui.screen.shared.LoadingContent
import com.cabovianco.remindme.presentation.viewmodel.MainViewModel
import java.time.Month
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MainScreen(
    onAddReminder: () -> Unit,
    onEditReminder: (Int) -> Unit,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedDate = uiState.selectedDate

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
            onEditReminder = onEditReminder,
            onDeleteReminder = { viewModel.deleteReminder(it) },
            uiState = uiState.mainState
        )
    }
}

@Composable
private fun MainContent(
    selectedDate: ZonedDateTime,
    onSelectedDateChange: (ZonedDateTime) -> Unit,
    selectableDates: List<ZonedDateTime>,
    onEditReminder: (Int) -> Unit,
    onDeleteReminder: (Reminder) -> Unit,
    uiState: MainState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        DateRangePicker(
            selectedDate = selectedDate,
            onSelectedDate = onSelectedDateChange,
            selectableDates = selectableDates,
            modifier = Modifier.fillMaxWidth()
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

                is MainState.Loading -> LoadingContent(
                    modifier = Modifier.align(
                        BiasAlignment(
                            0f,
                            -0.4f
                        )
                    )
                )

                is MainState.Error -> ErrorContent(
                    modifier = Modifier.align(
                        BiasAlignment(
                            0f,
                            -0.4f
                        )
                    )
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
            )
        ) {
            DateItemContent(
                date = date,
                isSelected = isSelected,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (isToday) {
            Spacer(modifier = Modifier.height(4.dp))
            TodayIndicator()
        }
    }
}

@Composable
private fun DateItemContent(
    date: ZonedDateTime,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val color =
        if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Column(
        modifier = modifier.padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                .capitalizeFirst(),
            style = MaterialTheme.typography.bodyMedium,
            color = color
        )

        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = color
        )
    }
}

@Composable
private fun TodayIndicator(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.main_date_today_indicator).uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun ReminderList(
    reminders: List<Reminder>,
    onEditReminder: (Int) -> Unit,
    onDeleteReminder: (Reminder) -> Unit,
    modifier: Modifier = Modifier
) {
    when (reminders) {
        emptyList<Reminder>() -> EmptyStateContent(
            text = stringResource(R.string.main_empty_state_label),
            icon = {
                Icon(
                    painter = painterResource(R.drawable.empty_state),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            },
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
    onEditReminder: (Int) -> Unit,
    onDeleteReminder: (Reminder) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(reminders, key = { it.id }) { reminder ->
            ReminderItem(
                reminder = reminder,
                onEdit = { onEditReminder(reminder.id) },
                onDelete = { onDeleteReminder(reminder) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderItem(
    reminder: Reminder,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.StartToEnd) onDelete()

            value == SwipeToDismissBoxValue.StartToEnd
        },
        positionalThreshold = { distance -> distance * 0.75f }
    )

    SwipeToDismissBox(
        modifier = modifier,
        state = dismissState,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = false,
        backgroundContent = { ReminderSwipeBackground(dismissState) }
    ) {
        ReminderEntry(
            reminder = reminder,
            onEdit = onEdit
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderSwipeBackground(
    state: SwipeToDismissBoxState,
    modifier: Modifier = Modifier
) {
    val direction = state.dismissDirection

    val color = when (direction) {
        SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
        SwipeToDismissBoxValue.EndToStart -> Color.Transparent
        else -> Color.Transparent
    }

    val alignment = when (direction) {
        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
        else -> Alignment.Center
    }

    val icon = when (direction) {
        SwipeToDismissBoxValue.StartToEnd -> painterResource(R.drawable.delete)
        else -> null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .padding(horizontal = 24.dp),
        contentAlignment = alignment
    ) {
        icon?.let {
            Icon(
                painter = it,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun ReminderEntry(
    reminder: Reminder,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardDefaults.shape),
        onClick = onEdit,
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
            date = reminder.dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        )
    }
}

@Composable
private fun ReminderEntryContent(
    title: String,
    description: String?,
    date: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        description?.let {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        DashedDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            color = MaterialTheme.colorScheme.inversePrimary
        )

        Text(
            text = date,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
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

private fun String.capitalizeFirst() = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
}
