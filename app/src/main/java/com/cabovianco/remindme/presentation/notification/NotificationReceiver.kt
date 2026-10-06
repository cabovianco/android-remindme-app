package com.cabovianco.remindme.presentation.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.cabovianco.remindme.data.alarm.AlarmScheduler
import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.model.ReminderHistoryEntry
import com.cabovianco.remindme.domain.model.ReminderRepeat
import com.cabovianco.remindme.domain.usecase.ClearOldHistoryUseCase
import com.cabovianco.remindme.domain.usecase.DeleteReminderUseCase
import com.cabovianco.remindme.domain.usecase.GetHistoryPreferencesUseCase
import com.cabovianco.remindme.domain.usecase.GetReminderByIdUseCase
import com.cabovianco.remindme.domain.usecase.InsertReminderHistoryUseCase
import com.cabovianco.remindme.domain.usecase.UpdateReminderUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {
    @Inject
    lateinit var getReminderByIdUseCase: GetReminderByIdUseCase

    @Inject
    lateinit var updateReminderUseCase: UpdateReminderUseCase

    @Inject
    lateinit var deleteReminderUseCase: DeleteReminderUseCase

    @Inject
    lateinit var getHistoryPreferencesUseCase: GetHistoryPreferencesUseCase

    @Inject
    lateinit var insertReminderHistoryUseCase: InsertReminderHistoryUseCase

    @Inject
    lateinit var clearOldHistoryUseCase: ClearOldHistoryUseCase

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra("id", -1)
        if (id == -1L) return

        val action = intent.action

        when (action) {
            ACTION_SNOOZE_15 -> handleSnooze(context, id, 15)
            ACTION_SNOOZE_60 -> handleSnooze(context, id, 60)
            ACTION_TRIGGER_SNOOZE, Intent.ACTION_BOOT_COMPLETED, null -> handleTrigger(id)
        }
    }

    private fun handleSnooze(context: Context, id: Long, minutes: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val dateTime = ZonedDateTime.now()
                .plusMinutes(minutes.toLong())
                .withSecond(0)
                .withNano(0)

            alarmScheduler.scheduleSnooze(id, dateTime)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(id.toInt())
        }
    }

    private fun handleTrigger(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val reminder = getReminderByIdUseCase(id).first() ?: return@launch

            notificationHelper.showNotification(
                id = reminder.id,
                title = reminder.title,
                message = reminder.description
            )

            processHistory(reminder)
            processRepeatOrCompletion(reminder)
        }
    }

    private suspend fun processHistory(reminder: Reminder) {
        val prefs = getHistoryPreferencesUseCase().first()

        if (prefs.isHistoryEnabled) {
            val entry = ReminderHistoryEntry(
                reminderId = reminder.id,
                title = reminder.title,
                description = reminder.description,
                triggeredAt = ZonedDateTime.now(),
                priority = reminder.priority,
                tags = reminder.tags
            )

            insertReminderHistoryUseCase(entry)
            clearOldHistoryUseCase(prefs.retentionDays)
        }
    }

    private suspend fun processRepeatOrCompletion(reminder: Reminder) {
        when (val repeat = reminder.repeat) {
            is ReminderRepeat.Never -> {
                deleteReminderUseCase(reminder)
            }

            else -> {
                val now = ZonedDateTime.now()
                    .withSecond(0)
                    .withNano(0)

                var nextDateTime = repeat.next(reminder.dateTime)

                while (!nextDateTime.isAfter(now)) {
                    nextDateTime = repeat.next(nextDateTime)
                }

                val nextReminder = reminder.copy(dateTime = nextDateTime)

                updateReminderUseCase(nextReminder)
                alarmScheduler.schedule(nextReminder)
            }
        }
    }

    companion object {
        const val ACTION_SNOOZE_15 = "com.cabovianco.remindme.ACTION_SNOOZE_15"
        const val ACTION_SNOOZE_60 = "com.cabovianco.remindme.ACTION_SNOOZE_60"
        const val ACTION_TRIGGER_SNOOZE = "com.cabovianco.remindme.ACTION_TRIGGER_SNOOZE"
    }
}
