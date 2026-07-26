package com.cabovianco.remindme.domain.usecase

import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.repository.ReminderRepository
import java.time.ZonedDateTime
import javax.inject.Inject

class UpdateReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(reminder: Reminder): Result<Unit> {
        val now = ZonedDateTime.now().withSecond(0).withNano(0)

        return if (reminder.dateTime.isAfter(now)) reminderRepository.update(reminder)
        else Result.failure(IllegalArgumentException("DateTime must be in the future"))
    }
}
