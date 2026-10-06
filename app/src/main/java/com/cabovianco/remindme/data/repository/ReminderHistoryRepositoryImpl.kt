package com.cabovianco.remindme.data.repository

import android.util.Log
import com.cabovianco.remindme.data.local.dao.ReminderHistoryDao
import com.cabovianco.remindme.data.local.entity.toDomain
import com.cabovianco.remindme.data.local.source.UserPreferencesDataSource
import com.cabovianco.remindme.domain.model.ReminderHistoryEntry
import com.cabovianco.remindme.domain.model.toEntity
import com.cabovianco.remindme.domain.repository.ReminderHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime
import javax.inject.Inject

private const val TAG = "ReminderHistoryRepository"

class ReminderHistoryRepositoryImpl @Inject constructor(
    private val reminderHistoryDao: ReminderHistoryDao,
    private val userPreferencesDataSource: UserPreferencesDataSource
) : ReminderHistoryRepository {
    override val isHistoryEnabled: Flow<Boolean> = userPreferencesDataSource.isHistoryEnabled

    override val historyRetentionDays: Flow<Int> = userPreferencesDataSource.historyRetentionDays

    override suspend fun setIsHistoryEnabled(enabled: Boolean) {
        userPreferencesDataSource.setIsHistoryEnabled(enabled)
    }

    override suspend fun setHistoryRetentionDays(days: Int) {
        userPreferencesDataSource.setHistoryRetentionDays(days)
    }

    override fun getHistory(): Flow<List<ReminderHistoryEntry>> =
        reminderHistoryDao.getAll()
            .map { it.map { entity -> entity.toDomain() } }
            .catch { ex ->
                Log.e(TAG, "ReminderHistoryRepository::getHistory", ex)
                throw ex
            }

    override suspend fun insertEntry(entry: ReminderHistoryEntry): Result<Long> = try {
        val id = reminderHistoryDao.insert(entry.toEntity())
        Result.success(id)

    } catch (ex: Exception) {
        Log.e(TAG, "ReminderHistoryRepository::insertEntry", ex)
        Result.failure(ex)
    }

    override suspend fun delete(entry: ReminderHistoryEntry): Result<Unit> = try {
        reminderHistoryDao.delete(entry.toEntity())
        Result.success(Unit)

    } catch (ex: Exception) {
        Log.e(TAG, "ReminderHistoryRepository::delete", ex)
        Result.failure(ex)
    }

    override suspend fun clearOldEntries(cutoffDate: ZonedDateTime): Result<Unit> = try {
        reminderHistoryDao.deleteOlderThan(cutoffDate)
        Result.success(Unit)

    } catch (ex: Exception) {
        Log.e(TAG, "ReminderHistoryRepository::clearOldEntries", ex)
        Result.failure(ex)
    }

    override suspend fun deleteAll(): Result<Unit> = try {
        reminderHistoryDao.deleteAll()
        Result.success(Unit)

    } catch (ex: Exception) {
        Log.e(TAG, "ReminderHistoryRepository::deleteAll", ex)
        Result.failure(ex)
    }
}
