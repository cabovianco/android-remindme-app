package com.cabovianco.remindme.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cabovianco.remindme.domain.model.Tag
import com.cabovianco.remindme.domain.model.TagColor
import com.cabovianco.remindme.domain.model.TagIcon

@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val color: TagColor,
    val icon: TagIcon?
)

fun TagEntity.toDomain() = Tag(id, name, color, icon)
