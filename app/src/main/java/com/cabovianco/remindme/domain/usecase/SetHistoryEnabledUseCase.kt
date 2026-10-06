package com.cabovianco.remindme.domain.usecase

import com.cabovianco.remindme.domain.repository.ReminderHistoryRepository
import javax.inject.Inject

class SetHistoryEnabledUseCase @Inject constructor(
    private val historyRepository: ReminderHistoryRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        historyRepository.setIsHistoryEnabled(enabled)
    }
}
