package com.cabovianco.remindme.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.cabovianco.remindme.domain.usecase.GetAllRemindersSinceDateUseCase
import com.cabovianco.remindme.service.AlarmScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    @Inject lateinit var getAllRemindersSinceDateUseCase: GetAllRemindersSinceDateUseCase
    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                val actualTime = ZonedDateTime.now()
                val reminders = getAllRemindersSinceDateUseCase(actualTime)
                    .first()

                reminders.forEach { alarmScheduler.schedule(it) }
            }
        }
    }
}
