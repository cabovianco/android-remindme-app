package com.cabovianco.remindme.domain.model

import java.time.ZonedDateTime

data class BackupData(
    val version: Int,
    val exportedAt: String,
    val tags: List<Tag>,
    val reminders: List<BackupReminder>
)

data class BackupReminder(
    val id: Long,
    val title: String,
    val description: String?,
    val dateTime: ZonedDateTime,
    val repeat: ReminderRepeat,
    val priority: ReminderPriority?,
    val tagIds: List<Long>
)
