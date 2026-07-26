package com.cabovianco.remindme.domain.usecase

import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime
import javax.inject.Inject

class GetAllRemindersSinceDateUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    operator fun invoke(from: ZonedDateTime): Flow<List<Reminder>> =
        reminderRepository.getAllSinceDate(from)
}
