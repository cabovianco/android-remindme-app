package com.cabovianco.remindme.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.model.ReminderPriority
import com.cabovianco.remindme.domain.model.ReminderRepeat
import java.time.ZonedDateTime

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String?,
    val dateTime: ZonedDateTime,
    val repeat: ReminderRepeat,
    val priority: ReminderPriority?
)

fun ReminderEntity.toDomain() = Reminder(id, title, description, dateTime, repeat, priority)
