package com.cabovianco.remindme.domain.usecase

import com.cabovianco.remindme.domain.repository.ReminderRepository
import java.time.ZonedDateTime
import javax.inject.Inject

class GetAllRemindersWithinDateRangeUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    operator fun invoke(from: ZonedDateTime, to: ZonedDateTime) =
        reminderRepository.getAllRemindersWithinDateRange(from, to)
}
