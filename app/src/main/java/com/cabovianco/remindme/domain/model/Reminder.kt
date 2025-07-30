package com.cabovianco.remindme.domain.model

import com.cabovianco.remindme.data.local.entity.ReminderEntity
import java.time.ZonedDateTime

data class Reminder(
    val id: Int,
    val title: String,
    val description: String?,
    val dateTime: ZonedDateTime,
    val repeat: Repeat
)

fun Reminder.toEntity() = ReminderEntity(id, title, description, dateTime, repeat)
