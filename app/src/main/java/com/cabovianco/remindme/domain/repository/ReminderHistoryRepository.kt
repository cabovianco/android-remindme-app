package com.cabovianco.remindme.domain.repository

import com.cabovianco.remindme.domain.model.ReminderHistoryEntry
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface ReminderHistoryRepository {
    val isHistoryEnabled: Flow<Boolean>
    val historyRetentionDays: Flow<Int>

    suspend fun setIsHistoryEnabled(enabled: Boolean)
    suspend fun setHistoryRetentionDays(days: Int)

    fun getHistory(): Flow<List<ReminderHistoryEntry>>
    fun getAll(): Flow<List<ReminderHistoryEntry>> = getHistory()
    suspend fun insertEntry(entry: ReminderHistoryEntry): Result<Long>
    suspend fun insert(entry: ReminderHistoryEntry): Result<Long> = insertEntry(entry)
    suspend fun delete(entry: ReminderHistoryEntry): Result<Unit>
    suspend fun clearOldEntries(cutoffDate: ZonedDateTime): Result<Unit>
    suspend fun deleteOlderThan(cutoffDate: ZonedDateTime): Result<Unit> = clearOldEntries(cutoffDate)
    suspend fun deleteAll(): Result<Unit>
}
