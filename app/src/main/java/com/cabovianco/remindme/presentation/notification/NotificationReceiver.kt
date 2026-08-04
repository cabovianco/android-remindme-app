package com.cabovianco.remindme.presentation.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.cabovianco.remindme.data.alarm.AlarmScheduler
import com.cabovianco.remindme.domain.model.ReminderRepeat
import com.cabovianco.remindme.domain.usecase.DeleteReminderUseCase
import com.cabovianco.remindme.domain.usecase.GetReminderByIdUseCase
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
    lateinit var notificationHelper: NotificationHelper
    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra("id", -1)
        val title = intent.getStringExtra("title") ?: ""
        val description = intent.getStringExtra("description")
        val action = intent.action

        if (action == ACTION_TRIGGER_SNOOZE) {
            notificationHelper.showNotification(id, title, description)
            return
        }

        if (action == ACTION_SNOOZE_15 || action == ACTION_SNOOZE_60) {
            val minutes = if (action == ACTION_SNOOZE_15) 15 else 60
            snooze(context, id, title, description, minutes)
            return
        }

        notificationHelper.showNotification(id, title, description)

        CoroutineScope(Dispatchers.IO).launch {
            val reminder = getReminderByIdUseCase(id)
                .first() ?: return@launch

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
    }

    private fun snooze(
        context: Context,
        id: Long,
        title: String,
        description: String?,
        minutes: Int
    ) {
        val newDateTime = ZonedDateTime.now()
            .plusMinutes(minutes.toLong())
            .withSecond(0)
            .withNano(0)

        alarmScheduler.scheduleSnooze(id, title, description, newDateTime)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancel(id.toInt())
    }

    companion object {
        const val ACTION_SNOOZE_15 = "com.cabovianco.remindme.ACTION_SNOOZE_15"
        const val ACTION_SNOOZE_60 = "com.cabovianco.remindme.ACTION_SNOOZE_60"
        const val ACTION_TRIGGER_SNOOZE = "com.cabovianco.remindme.ACTION_TRIGGER_SNOOZE"
    }
}
