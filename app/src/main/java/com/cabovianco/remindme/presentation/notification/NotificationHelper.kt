package com.cabovianco.remindme.presentation.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.cabovianco.remindme.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val CHANNEL_ID = "reminders"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "RemindMe",
            NotificationManager.IMPORTANCE_HIGH
        )

        notificationManager.createNotificationChannel(channel)
    }

    fun showNotification(id: Long, title: String, message: String?) {
        val snooze15Intent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_SNOOZE_15
            putExtra("id", id)
            putExtra("title", title)
            putExtra("description", message)
        }

        val snooze15PendingIntent = PendingIntent.getBroadcast(
            context,
            id.toInt() * 10 + 1,
            snooze15Intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snooze60Intent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_SNOOZE_60
            putExtra("id", id)
            putExtra("title", title)
            putExtra("description", message)
        }

        val snooze60PendingIntent = PendingIntent.getBroadcast(
            context,
            id.toInt() * 10 + 2,
            snooze60Intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .addAction(
                0,
                context.getString(R.string.snooze_15_min),
                snooze15PendingIntent
            )
            .addAction(
                0,
                context.getString(R.string.snooze_1_hour),
                snooze60PendingIntent
            )

        if (message != null) {
            notification.setContentText(message)
        }

        notificationManager.notify(id.toInt(), notification.build())
    }
}
