package com.cabovianco.remindme.data.local.converter

import androidx.room.TypeConverter
import com.cabovianco.remindme.domain.model.Repeat

enum class RepeatType {
    NEVER,
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}

class RepeatConverter {
    @TypeConverter
    fun fromRepeat(repeat: Repeat?) = when (repeat) {
        Repeat.Never -> RepeatType.NEVER
        Repeat.Daily -> RepeatType.DAILY
        Repeat.Weekly -> RepeatType.WEEKLY
        Repeat.Monthly -> RepeatType.MONTHLY
        Repeat.Yearly -> RepeatType.YEARLY
        null -> null
    }

    @TypeConverter
    fun toRepeat(repeatType: RepeatType?): Repeat? = when (repeatType) {
        RepeatType.NEVER -> Repeat.Never
        RepeatType.DAILY -> Repeat.Daily
        RepeatType.WEEKLY -> Repeat.Weekly
        RepeatType.MONTHLY -> Repeat.Monthly
        RepeatType.YEARLY -> Repeat.Yearly
        null -> null
    }
}
