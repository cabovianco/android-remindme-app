package com.cabovianco.remindme.presentation.ui.util

import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

fun ZonedDateTime.atStartOfDay(): ZonedDateTime =
    withHour(0).withMinute(0).withSecond(0).withNano(0)

fun ZonedDateTime.atEndOfDay(): ZonedDateTime =
    withHour(23).withMinute(59).withSecond(59).withNano(999999999)

fun ZonedDateTime.atStartOfWeek(): ZonedDateTime {
    val daysFromSunday = dayOfWeek.value.toLong() % 7
    return minusDays(daysFromSunday)
        .withHour(0)
        .withMinute(0)
        .withSecond(0)
        .withNano(0)
}

fun ZonedDateTime.atEndOfWeek(): ZonedDateTime {
    val daysFromSunday = dayOfWeek.value.toLong() % 7
    return plusDays(6 - daysFromSunday)
        .withHour(23)
        .withMinute(59)
        .withSecond(59)
        .withNano(999999999)
}

fun ZonedDateTime.weekDatesForOffset(offset: Int): List<ZonedDateTime> {
    val weekStart = atStartOfWeek().plusWeeks(offset.toLong())
    return (0..6).map { weekStart.plusDays(it.toLong()) }
}

fun ZonedDateTime.weekOffsetFrom(referenceDate: ZonedDateTime): Int {
    val referenceStartOfWeek = referenceDate.atStartOfWeek()
    val dateStartOfWeek = atStartOfWeek()
    return ChronoUnit.WEEKS.between(
        referenceStartOfWeek,
        dateStartOfWeek
    ).toInt()
}
