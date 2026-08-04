package com.cabovianco.remindme.data.repository

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.cabovianco.remindme.domain.repository.PermissionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PermissionRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : PermissionRepository {
    override fun hasNotificationPermission(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

    override fun hasExactAlarmPermission(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
}
