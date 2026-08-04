package com.cabovianco.remindme.domain.repository

interface PermissionRepository {
    fun hasNotificationPermission(): Boolean
    fun hasExactAlarmPermission(): Boolean
}
