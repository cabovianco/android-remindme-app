package com.cabovianco.remindme.presentation.notification

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

        CoroutineScope(Dispatchers.IO).launch {
            val reminder = getReminderByIdUseCase(id)
                .first() ?: return@launch

            notificationHelper.showNotification(id, reminder.title, reminder.description)

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
}
