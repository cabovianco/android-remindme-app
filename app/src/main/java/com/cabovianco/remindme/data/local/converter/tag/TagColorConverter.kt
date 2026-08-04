package com.cabovianco.remindme.data.local.converter.tag

import androidx.room.TypeConverter
import com.cabovianco.remindme.domain.model.TagColor
import kotlinx.serialization.json.Json

class TagColorConverter {
    @TypeConverter
    fun fromTagColor(color: TagColor): String =
        Json.encodeToString(TagColor.serializer(), color)

    @TypeConverter
    fun toTagColor(value: String): TagColor =
        Json.decodeFromString(TagColor.serializer(), value)
}
