package com.cabovianco.remindme.data.local.converter.reminder

import androidx.room.TypeConverter
import com.cabovianco.remindme.domain.model.ReminderRepeat
import kotlinx.serialization.json.Json

class ReminderRepeatConverter {
    @TypeConverter
    fun fromReminderRepeat(repeat: ReminderRepeat): String =
        Json.encodeToString(ReminderRepeat.serializer(), repeat)

    @TypeConverter
    fun toReminderRepeat(value: String): ReminderRepeat =
        Json.decodeFromString(ReminderRepeat.serializer(), value)
}
