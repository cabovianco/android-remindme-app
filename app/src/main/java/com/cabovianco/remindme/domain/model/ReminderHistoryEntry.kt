package com.cabovianco.remindme.domain.model

import com.cabovianco.remindme.data.local.entity.ReminderHistoryEntryEntity
import java.time.ZonedDateTime

data class ReminderHistoryEntry(
    val id: Long = 0,
    val reminderId: Long,
    val title: String,
    val description: String?,
    val triggeredAt: ZonedDateTime,
    val priority: ReminderPriority?,
    val tags: List<Tag> = emptyList()
)

fun ReminderHistoryEntry.toEntity() = ReminderHistoryEntryEntity(
    id = id,
    reminderId = reminderId,
    title = title,
    description = description,
    triggeredAt = triggeredAt,
    priority = priority,
    tags = tags
)
