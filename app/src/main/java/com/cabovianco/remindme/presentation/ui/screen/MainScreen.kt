package com.cabovianco.remindme.presentation.ui.screen

import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cabovianco.remindme.R
import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.presentation.state.RemindersUiState
import com.cabovianco.remindme.presentation.ui.theme.CherryRegular
import java.time.Month
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun MainScreen(
    onAddReminderButtonClick: () -> Unit,
    onEditReminderClick: (Int) -> Unit,
    onDeleteReminderClick: (Reminder) -> Unit,
    onBackDaySelectorButtonClick: () -> Unit,
    onForwardDaySelectorButtonClick: () -> Unit,
    selectableDays: List<ZonedDateTime>,
    remindersUiState: RemindersUiState,
    modifier: Modifier = Modifier
) {
    when (remindersUiState) {
        is RemindersUiState.Success -> {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopBar(
                    onAddButtonClick = { onAddReminderButtonClick() },
                    modifier = Modifier.fillMaxWidth()
                )

                var selectedDay by rememberSaveable {
                    mutableStateOf(
                        ZonedDateTime.now()
                            .withHour(0)
                            .withMinute(0)
                            .withSecond(0)
                            .withNano(0)
                    )
                }

                Date(
                    year = selectedDay.year,
                    month = selectedDay.month.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                DaySelector(
                    onBackButtonClick = onBackDaySelectorButtonClick,
                    onForwardButtonClick = onForwardDaySelectorButtonClick,
                    selectedDay = selectedDay,
                    onSelectedDay = { selectedDay = it },
                    selectableDays = selectableDays,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                ListReminders(
                    reminders = remindersUiState.reminders
                        .filter {
                            it.dateTime.isAfter(selectedDay.minusMinutes(1)) &&
                                    it.dateTime.isBefore(selectedDay.plusDays(1))
                        }
                        .sortedBy { it.dateTime },
                    onEditReminderClick = onEditReminderClick,
                    onDeleteReminderClick = onDeleteReminderClick,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        is RemindersUiState.Loading -> OnLoading(modifier = modifier)
        is RemindersUiState.Error -> OnError(modifier = modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onAddButtonClick: () -> Unit, modifier: Modifier = Modifier) {
    TopAppBar(
        modifier = modifier,
        title = {},
        actions = {
            FilledIconButton(
                modifier = Modifier.padding(horizontal = 8.dp),
                onClick = { onAddButtonClick() },
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.add),
                    contentDescription = null
                )
            }
        },
        windowInsets = WindowInsets(top = 0.dp),
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

private fun getMonthResId(month: String): Int = when (month) {
    Month.APRIL.name -> R.string.april
    Month.AUGUST.name -> R.string.august
    Month.DECEMBER.name -> R.string.december
    Month.FEBRUARY.name -> R.string.february
    Month.JANUARY.name -> R.string.january
    Month.JULY.name -> R.string.july
    Month.JUNE.name -> R.string.june
    Month.MARCH.name -> R.string.march
    Month.MAY.name -> R.string.may
    Month.NOVEMBER.name -> R.string.november
    Month.OCTOBER.name -> R.string.october
    Month.SEPTEMBER.name -> R.string.september
    else -> 0
}

@Composable
private fun Date(year: Int, month: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = "$year",
            fontSize = 18.sp,
            fontFamily = CherryRegular
        )

        Text(
            text = stringResource(getMonthResId(month)),
            fontSize = 32.sp,
            fontFamily = CherryRegular
        )
    }
}

@Composable
private fun DaySelector(
    onBackButtonClick: () -> Unit,
    onForwardButtonClick: () -> Unit,
    selectedDay: ZonedDateTime,
    onSelectedDay: (ZonedDateTime) -> Unit,
    selectableDays: List<ZonedDateTime>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DaySelectorButton(iconResId = R.drawable.arrow_left, onClick = onBackButtonClick)

            selectableDays.forEach {
                Card(
                    onClick = { onSelectedDay(it) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedDay.dayOfMonth == it.dayOfMonth)
                            MaterialTheme.colorScheme.primary else Color.Transparent
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${it.dayOfWeek.name[0]}",
                            style = MaterialTheme.typography.titleSmall
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = it.format(DateTimeFormatter.ofPattern("dd")),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }

            DaySelectorButton(iconResId = R.drawable.arrow_right, onClick = onForwardButtonClick)
        }
    }
}

@Composable
private fun DaySelectorButton(iconResId: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(iconResId),
            contentDescription = null
        )
    }
}

@Composable
private fun ListReminders(
    reminders: List<Reminder>,
    onEditReminderClick: (Int) -> Unit,
    onDeleteReminderClick: (Reminder) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        reminders.forEach {
            item {
                ReminderItem(
                    reminder = it,
                    onEditReminderClick = onEditReminderClick,
                    onDeleteReminderClick = onDeleteReminderClick,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ReminderItem(
    reminder: Reminder,
    onEditReminderClick: (Int) -> Unit,
    onDeleteReminderClick: (Reminder) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    if (showDeleteDialog) {
        DeleteDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = { onDeleteReminderClick(reminder) }
        )
    }

    ElevatedCard(
        modifier = modifier
            .clip(CardDefaults.shape)
            .combinedClickable(
                onClick = { onEditReminderClick(reminder.id) },
                onLongClick = {
                    val vibrator = context.getSystemService(Vibrator::class.java)
                    vibrator?.vibrate(
                        VibrationEffect.createOneShot(
                            50,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )

                    showDeleteDialog = true
                }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.80f)
            ) {
                Text(text = reminder.title, style = MaterialTheme.typography.titleMedium)

                reminder.description?.let { description ->
                    Text(
                        text = reminder.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Card {
                Text(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    text = reminder.dateTime.format(
                        DateTimeFormatter.ofPattern("HH:mm")
                    ),
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

@Composable
private fun DeleteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.delete_dialog_title))
        },
        text = {
            Text(text = stringResource(R.string.delete_dialog_description))
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(text = stringResource(R.string.delete_dialog_button))
            }
        }
    )
}

@Composable
private fun OnLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun OnError(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.error),
            contentDescription = null
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(text = stringResource(R.string.error_message))
    }
}
