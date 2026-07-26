package com.cabovianco.remindme.domain.repository

import com.cabovianco.remindme.domain.model.Reminder
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface ReminderRepository {
    fun getAll(): Flow<List<Reminder>>

    fun getAllSinceDate(from: ZonedDateTime): Flow<List<Reminder>>

    fun getById(id: Long): Flow<Reminder?>

    suspend fun insert(reminder: Reminder): Result<Long>

    suspend fun update(reminder: Reminder): Result<Unit>

    suspend fun delete(reminder: Reminder): Result<Unit>
}
