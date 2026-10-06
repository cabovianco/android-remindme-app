package com.cabovianco.remindme.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cabovianco.remindme.domain.model.ReminderHistoryEntry
import com.cabovianco.remindme.domain.model.ReminderPriority
import com.cabovianco.remindme.domain.model.Tag
import java.time.ZonedDateTime

@Entity(tableName = "reminder_entries")
data class ReminderHistoryEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val reminderId: Long,
    val title: String,
    val description: String?,
    val triggeredAt: ZonedDateTime,
    val priority: ReminderPriority?,
    val tags: List<Tag> = emptyList()
)

fun ReminderHistoryEntryEntity.toDomain() = ReminderHistoryEntry(
    id = id,
    reminderId = reminderId,
    title = title,
    description = description,
    triggeredAt = triggeredAt,
    priority = priority,
    tags = tags
)
