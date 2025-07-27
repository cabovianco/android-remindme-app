package com.cabovianco.remindme.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.cabovianco.remindme.domain.model.Repeat
import com.cabovianco.remindme.domain.model.toRepeat
import com.cabovianco.remindme.domain.usecase.GetReminderByIdUseCase
import com.cabovianco.remindme.service.AlarmScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {
    @Inject lateinit var getReminderByIdUseCase: GetReminderByIdUseCase
    @Inject lateinit var notificationHelper: NotificationHelper
    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra("id", -1)

        CoroutineScope(Dispatchers.IO).launch {
            val reminder = getReminderByIdUseCase(id)
                .first() ?: return@launch

            notificationHelper.showNotification(id, reminder.title, reminder.description)

            val repeat = reminder.repeat.toRepeat()
            if (repeat !is Repeat.Never) {
                val nextDateTime = repeat.nextDate(ZonedDateTime.now())
                val nextReminder = reminder.copy(dateTime = nextDateTime)

                alarmScheduler.schedule(nextReminder)
            }
        }
    }
}
