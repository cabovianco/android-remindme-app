package com.cabovianco.remindme.domain.usecase

import com.cabovianco.remindme.domain.model.ReminderHistoryEntry
import com.cabovianco.remindme.domain.repository.ReminderHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReminderHistoryUseCase @Inject constructor(
    private val historyRepository: ReminderHistoryRepository
) {
    operator fun invoke(): Flow<List<ReminderHistoryEntry>> =
        historyRepository.getHistory()
}
