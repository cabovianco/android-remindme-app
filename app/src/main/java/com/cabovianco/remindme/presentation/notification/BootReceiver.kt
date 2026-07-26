package com.cabovianco.remindme.presentation.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.cabovianco.remindme.data.alarm.AlarmScheduler
import com.cabovianco.remindme.domain.model.ReminderRepeat
import com.cabovianco.remindme.domain.usecase.GetAllRemindersUseCase
import com.cabovianco.remindme.domain.usecase.UpdateReminderUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    @Inject
    lateinit var getAllRemindersUseCase: GetAllRemindersUseCase

    @Inject
    lateinit var updateReminderUseCase: UpdateReminderUseCase

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                val now = ZonedDateTime.now()
                    .withSecond(0)
                    .withNano(0)

                val reminders = getAllRemindersUseCase().first()

                reminders.forEach { reminder ->
                    var currentReminder = reminder

                    while (
                        !currentReminder.dateTime.isAfter(now) &&
                        currentReminder.repeat !is ReminderRepeat.Never
                    ) {
                        currentReminder = currentReminder.copy(
                            dateTime = currentReminder.repeat.next(currentReminder.dateTime)
                        )
                    }

                    if (currentReminder.dateTime.isAfter(now)) {
                        if (currentReminder.dateTime != reminder.dateTime) {
                            updateReminderUseCase(currentReminder)
                        }

                        alarmScheduler.schedule(currentReminder)
                    }
                }
            }
        }
    }
}
