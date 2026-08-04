package com.cabovianco.remindme.domain.model

import com.cabovianco.remindme.data.local.entity.ReminderEntity
import java.time.ZonedDateTime

data class Reminder(
    val id: Long,
    val title: String,
    val description: String?,
    val dateTime: ZonedDateTime,
    val repeat: ReminderRepeat,
    val priority: ReminderPriority? = null,
    val tags: List<Tag> = emptyList()
)

fun Reminder.toEntity() = ReminderEntity(id, title, description, dateTime, repeat, priority)
