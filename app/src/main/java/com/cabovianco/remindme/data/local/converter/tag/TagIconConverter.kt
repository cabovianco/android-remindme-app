package com.cabovianco.remindme.data.local.converter.tag

import androidx.room.TypeConverter
import com.cabovianco.remindme.domain.model.TagIcon
import kotlinx.serialization.json.Json

class TagIconConverter {
    @TypeConverter
    fun fromTagIcon(icon: TagIcon?): String? =
        icon?.let { Json.encodeToString(TagIcon.serializer(), it) }

    @TypeConverter
    fun toTagIcon(value: String?): TagIcon? =
        value?.let { Json.decodeFromString(TagIcon.serializer(), it) }
}
