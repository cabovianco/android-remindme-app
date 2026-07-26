package com.cabovianco.remindme.domain.usecase

import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.repository.ReminderRepository
import java.time.ZonedDateTime
import javax.inject.Inject

class InsertReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(reminder: Reminder): Result<Long> {
        val now = ZonedDateTime.now().withSecond(0).withNano(0)

        return if (reminder.dateTime.isAfter(now)) reminderRepository.insert(reminder)
        else Result.failure(IllegalArgumentException("DateTime must be in the future"))
    }
}
