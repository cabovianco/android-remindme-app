package com.cabovianco.remindme.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.model.Repeat
import java.time.ZonedDateTime

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String?,
    val dateTime: ZonedDateTime,
    val repeat: Repeat
)

fun ReminderEntity.toDomain() = Reminder(id, title, description, dateTime, repeat)
