package com.cabovianco.remindme.domain.usecase

import com.cabovianco.remindme.domain.repository.ReminderHistoryRepository
import javax.inject.Inject

class SetHistoryRetentionDaysUseCase @Inject constructor(
    private val historyRepository: ReminderHistoryRepository
) {
    suspend operator fun invoke(days: Int) {
        historyRepository.setHistoryRetentionDays(days)
    }
}
