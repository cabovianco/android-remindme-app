package com.cabovianco.remindme.domain.model

import java.time.ZonedDateTime

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
