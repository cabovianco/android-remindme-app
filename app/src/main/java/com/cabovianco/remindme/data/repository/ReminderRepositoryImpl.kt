package com.cabovianco.remindme.data.repository

import android.util.Log
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

private const val TAG = "ReminderRepository"

class ReminderRepositoryImpl @Inject constructor(
    private val reminderDao: ReminderDao
) : ReminderRepository {
    override fun getAll(): Flow<List<Reminder>> = reminderDao.getAll()
        .map { it.map { entity -> entity.toDomain() } }
        .catch { ex ->
            Log.e(TAG, "ReminderRepository::getAll", ex)
            throw ex
        }

    override fun getAllSinceDate(from: ZonedDateTime): Flow<List<Reminder>> =
        reminderDao.getAllSinceDate(from)
            .map { it.map { reminder -> reminder.toDomain() } }
            .catch { ex ->
                Log.e(TAG, "ReminderRepository::getAllSinceDate", ex)
                throw ex
            }

    override fun getById(id: Long): Flow<Reminder?> = reminderDao.getById(id)
        .map { it?.toDomain() }
        .catch { ex ->
            Log.e(TAG, "ReminderRepository::getById", ex)
            throw ex
        }

    override suspend fun insert(reminder: Reminder): Result<Long> = try {
        val id = reminderDao.insertWithTags(
            reminder = reminder.toEntity(),
            tagIds = reminder.tags.map { it.id }
        )

        Result.success(id)

    } catch (ex: Exception) {
        Log.e(TAG, "ReminderRepository::insert", ex)
        Result.failure(ex)
    }

    override suspend fun update(reminder: Reminder): Result<Unit> = try {
        reminderDao.updateWithTags(
            reminder = reminder.toEntity(),
            tagIds = reminder.tags.map { it.id }
        )

        Result.success(Unit)

    } catch (ex: Exception) {
        Log.e(TAG, "ReminderRepository::update", ex)
        Result.failure(ex)
    }

    override suspend fun delete(reminder: Reminder): Result<Unit> = try {
        reminderDao.delete(reminder.toEntity())
        Result.success(Unit)

    } catch (ex: Exception) {
        Log.e(TAG, "ReminderRepository::deleteReminder", ex)
        Result.failure(ex)
    }
}
