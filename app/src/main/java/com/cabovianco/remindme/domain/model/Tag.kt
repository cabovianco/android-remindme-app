package com.cabovianco.remindme.domain.model

import com.cabovianco.remindme.data.local.entity.TagEntity

data class Tag(
    val id: Long = 0,
    val name: String,
    val color: TagColor = TagColor.Blue,
    val icon: TagIcon? = null
)

fun Tag.toEntity() = TagEntity(id, name, color, icon)
