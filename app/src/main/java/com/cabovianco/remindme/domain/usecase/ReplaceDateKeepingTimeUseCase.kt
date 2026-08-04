package com.cabovianco.remindme.domain.usecase

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

class ReplaceDateKeepingTimeUseCase @Inject constructor() {
    operator fun invoke(currentDateTime: ZonedDateTime, selectedDateMillis: Long): ZonedDateTime {
        val date = Instant.ofEpochMilli(selectedDateMillis)
            .atZone(ZoneId.of("UTC"))
            .toLocalDate()

        return currentDateTime
            .withYear(date.year)
            .withMonth(date.monthValue)
            .withDayOfMonth(date.dayOfMonth)
    }
}
