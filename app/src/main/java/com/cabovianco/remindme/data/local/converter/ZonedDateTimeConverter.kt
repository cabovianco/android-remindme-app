package com.cabovianco.remindme.data.local.converter

import androidx.room.TypeConverter
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
