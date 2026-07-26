package com.cabovianco.remindme.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ReminderWithTags(
    @Embedded val reminder: ReminderEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ReminderTagCrossRef::class,
            parentColumn = "reminderId",
            entityColumn = "tagId"
        )
    )
    val tags: List<TagEntity>
)

fun ReminderWithTags.toDomain() = reminder.toDomain().copy(
    tags = tags.map { it.toDomain() }
)
