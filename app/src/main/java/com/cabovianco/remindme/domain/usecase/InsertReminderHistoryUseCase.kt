package com.cabovianco.remindme.domain.usecase

import com.cabovianco.remindme.domain.model.ReminderHistoryEntry
import com.cabovianco.remindme.domain.repository.ReminderHistoryRepository
import javax.inject.Inject

class InsertReminderHistoryUseCase @Inject constructor(
    private val historyRepository: ReminderHistoryRepository
) {
    suspend operator fun invoke(entry: ReminderHistoryEntry): Result<Long> =
        historyRepository.insertEntry(entry)
}
