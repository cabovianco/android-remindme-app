package com.cabovianco.remindme.domain.usecase

import com.cabovianco.remindme.domain.repository.ReminderHistoryRepository
import kotlinx.coroutines.flow.first
import java.time.ZonedDateTime
import javax.inject.Inject

class ClearOldHistoryUseCase @Inject constructor(
    private val historyRepository: ReminderHistoryRepository
) {
    suspend operator fun invoke(retentionDays: Int? = null): Result<Unit> {
        val days = retentionDays ?: historyRepository.historyRetentionDays.first()
        val cutoffDate = ZonedDateTime.now().minusDays(days.toLong())

        return historyRepository.clearOldEntries(cutoffDate)
    }
}
