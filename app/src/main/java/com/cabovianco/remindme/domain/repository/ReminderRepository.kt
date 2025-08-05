package com.cabovianco.remindme.domain.repository

import com.cabovianco.remindme.domain.model.Reminder
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface ReminderRepository {
    suspend fun insertReminder(reminder: Reminder): Result<Long>

    suspend fun updateReminder(reminder: Reminder): Result<Unit>

    fun getReminderById(id: Int): Flow<Reminder?>

    fun getAllRemindersSinceDate(from: ZonedDateTime): Flow<List<Reminder>>

    fun getAllReminders(): Flow<List<Reminder>>

    suspend fun deleteReminder(reminder: Reminder): Result<Unit>
}
