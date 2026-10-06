package com.cabovianco.remindme.data.local.converter.tag

import androidx.room.TypeConverter
import com.cabovianco.remindme.domain.model.Tag
import kotlinx.serialization.json.Json

class TagListConverter {
    @TypeConverter
    fun fromTagList(tags: List<Tag>): String =
        Json.encodeToString(tags)

    @TypeConverter
    fun toTagList(value: String): List<Tag> =
        try {
            Json.decodeFromString(value)

        } catch (_: Exception) {
            emptyList()
        }
}
