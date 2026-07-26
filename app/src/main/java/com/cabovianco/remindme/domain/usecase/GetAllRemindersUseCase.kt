package com.cabovianco.remindme.domain.usecase

import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllRemindersUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    operator fun invoke(): Flow<List<Reminder>> =
        reminderRepository.getAll()
}
