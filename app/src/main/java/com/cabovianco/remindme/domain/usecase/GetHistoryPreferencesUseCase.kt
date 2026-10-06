package com.cabovianco.remindme.domain.usecase

import com.cabovianco.remindme.domain.repository.ReminderHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class HistoryPreferences(
    val isHistoryEnabled: Boolean,
    val retentionDays: Int
)

class GetHistoryPreferencesUseCase @Inject constructor(
    private val historyRepository: ReminderHistoryRepository
) {
    operator fun invoke(): Flow<HistoryPreferences> =
        combine(
            historyRepository.isHistoryEnabled,
            historyRepository.historyRetentionDays
        ) { enabled, days ->
            HistoryPreferences(enabled, days)
        }
}
