package com.cabovianco.remindme.data.local.converter

import androidx.room.TypeConverter
import com.cabovianco.remindme.domain.model.ReminderRepeat
import kotlinx.serialization.json.Json
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ZonedDateTimeConverter {
    @TypeConverter
    fun fromZonedDateTime(zonedDateTime: ZonedDateTime?) =
        zonedDateTime?.format(DateTimeFormatter.ISO_ZONED_DATE_TIME)

    @TypeConverter
    fun toZonedDateTime(zonedDateTimeString: String?) =
        zonedDateTimeString?.let {
            ZonedDateTime.parse(it, DateTimeFormatter.ISO_ZONED_DATE_TIME)
        }
}

class ReminderRepeatConverter {
    @TypeConverter
    fun fromReminderRepeat(repeat: ReminderRepeat): String =
        Json.encodeToString(ReminderRepeat.serializer(), repeat)

    @TypeConverter
    fun toReminderRepeat(value: String): ReminderRepeat =
        Json.decodeFromString(ReminderRepeat.serializer(), value)
}
