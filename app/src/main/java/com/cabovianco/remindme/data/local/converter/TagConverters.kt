package com.cabovianco.remindme.data.local.converter

import androidx.room.TypeConverter
import com.cabovianco.remindme.domain.model.TagColor
import com.cabovianco.remindme.domain.model.TagIcon
import kotlinx.serialization.json.Json

class TagColorConverter {
    @TypeConverter
    fun fromTagColor(color: TagColor): String =
        Json.encodeToString(TagColor.serializer(), color)

    @TypeConverter
    fun toTagColor(value: String): TagColor =
        Json.decodeFromString(TagColor.serializer(), value)
}

class TagIconConverter {
    @TypeConverter
    fun fromTagIcon(icon: TagIcon?): String? =
        icon?.let { Json.encodeToString(TagIcon.serializer(), it) }

    @TypeConverter
    fun toTagIcon(value: String?): TagIcon? =
        value?.let { Json.decodeFromString(TagIcon.serializer(), it) }
}
