package com.cabovianco.remindme.data.local.converter.reminder

import androidx.room.TypeConverter
import com.cabovianco.remindme.domain.model.ReminderPriority

class ReminderPriorityConverter {
    @TypeConverter
    fun fromReminderPriority(priority: ReminderPriority?): String? = priority?.name

    @TypeConverter
    fun toReminderPriority(value: String?): ReminderPriority? = value?.let { ReminderPriority.valueOf(it) }
}
