package com.cabovianco.remindme.data.repository

import com.cabovianco.remindme.data.local.dao.ReminderDao
import com.cabovianco.remindme.data.local.entity.toDomain
import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.model.toEntity
import com.cabovianco.remindme.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val reminderDao: ReminderDao
) : ReminderRepository {
    override suspend fun insertReminder(reminder: Reminder): Result<Long> {
        return try {
            val id = reminderDao.insert(reminder.toEntity())
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateReminder(reminder: Reminder): Result<Unit> {
        return try {
            reminderDao.update(reminder.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getReminderById(id: Int): Flow<Reminder?> {
        return reminderDao.getById(id)
            .map { it?.toDomain() }
            .catch { e ->
                throw e
            }
    }

    override fun getAllRemindersSinceDate(from: ZonedDateTime): Flow<List<Reminder>> {
        return reminderDao.getAllSinceDate(from)
            .map { it.map { entity -> entity.toDomain() } }
            .catch { e ->
                throw e
            }
    }

    override fun getAllReminders(): Flow<List<Reminder>> {
        return reminderDao.getAll()
            .map { it.map { entity -> entity.toDomain() } }
            .catch { e ->
                throw e
            }
    }

    override suspend fun deleteReminder(reminder: Reminder): Result<Unit> {
        return try {
            reminderDao.delete(reminder.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
