package com.cabovianco.remindme.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.presentation.notification.NotificationReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    fun schedule(reminder: Reminder) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("id", reminder.id)
            putExtra("title", reminder.title)
            putExtra("description", reminder.description)
        }

        scheduleAlarm(reminder.id.toInt(), reminder.dateTime, intent)
    }

    fun scheduleSnooze(id: Long, title: String, description: String?, dateTime: ZonedDateTime) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_TRIGGER_SNOOZE
            putExtra("id", id)
            putExtra("title", title)
            putExtra("description", description)
        }
        scheduleAlarm(id.toInt(), dateTime, intent)
    }

    private fun scheduleAlarm(requestCode: Int, dateTime: ZonedDateTime, intent: Intent) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            return
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            dateTime.toInstant().toEpochMilli(),
            pendingIntent
        )
    }

    fun cancel(id: Long) {
        val intent = Intent(context, NotificationReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}
