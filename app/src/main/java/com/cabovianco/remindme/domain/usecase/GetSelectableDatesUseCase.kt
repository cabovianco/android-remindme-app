package com.cabovianco.remindme.domain.usecase

import java.time.ZonedDateTime
import javax.inject.Inject

class GetSelectableDatesUseCase @Inject constructor() {
    operator fun invoke(from: ZonedDateTime, to: ZonedDateTime): List<ZonedDateTime> =
        generateSequence(from) { it.plusDays(1) }
            .takeWhile { !it.isAfter(to) }
            .toList()
}
