package com.cabovianco.remindme.domain.model

import java.time.ZonedDateTime

enum class RepeatType {
    NEVER, DAILY, WEEKLY, MONTHLY, YEARLY
}

sealed class Repeat {
    abstract fun nextDate(from: ZonedDateTime): ZonedDateTime

    object Never : Repeat() {
        override fun nextDate(from: ZonedDateTime): ZonedDateTime = from
    }

    object Daily : Repeat() {
        override fun nextDate(from: ZonedDateTime): ZonedDateTime = from.plusDays(1)
    }

    object Weekly : Repeat() {
        override fun nextDate(from: ZonedDateTime): ZonedDateTime = from.plusWeeks(1)
    }

    object Monthly : Repeat() {
        override fun nextDate(from: ZonedDateTime): ZonedDateTime = from.plusMonths(1)
    }

    object Yearly : Repeat() {
        override fun nextDate(from: ZonedDateTime): ZonedDateTime = from.plusYears(1)
    }
}

fun RepeatType.toRepeat() = when (this) {
    RepeatType.NEVER -> Repeat.Never
    RepeatType.DAILY -> Repeat.Daily
    RepeatType.WEEKLY -> Repeat.Weekly
    RepeatType.MONTHLY -> Repeat.Monthly
    RepeatType.YEARLY -> Repeat.Yearly
}
