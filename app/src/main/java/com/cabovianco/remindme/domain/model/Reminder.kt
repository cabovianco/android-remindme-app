package com.cabovianco.remindme.domain.model

import com.cabovianco.remindme.data.local.entity.ReminderEntity
import java.time.ZonedDateTime

data class Reminder(
    val id: Int = 0,
    val title: String,
    val description: String? = null,
    val dateTime: ZonedDateTime,
    val repeat: RepeatType
)

fun Reminder.toEntity() = ReminderEntity(id, title, description, dateTime, repeat)
