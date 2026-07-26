package com.cabovianco.remindme.domain.usecase

import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReminderByIdUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    operator fun invoke(id: Long): Flow<Reminder?> =
        reminderRepository.getById(id)
}
