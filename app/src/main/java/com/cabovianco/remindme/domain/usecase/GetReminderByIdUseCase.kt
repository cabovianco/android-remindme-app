package com.cabovianco.remindme.domain.usecase

import com.cabovianco.remindme.domain.repository.ReminderRepository
import javax.inject.Inject

class GetReminderByIdUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    operator fun invoke(id: Int) =
        reminderRepository.getReminderById(id)
}
