package com.cabovianco.remindme.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cabovianco.remindme.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: ReminderEntity): Long

    @Update
    suspend fun update(reminder: ReminderEntity): Long

    @Query("SELECT * FROM reminders WHERE id = :id")
    fun getById(id: Int): Flow<ReminderEntity?>

    @Query("SELECT * FROM reminders WHERE dateTime >= :from AND dateTime <= :to")
    fun getAllWithinDateRange(
        from: ZonedDateTime,
        to: ZonedDateTime
    ): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE dateTime > :from")
    fun getAllSinceDate(from: ZonedDateTime): Flow<List<ReminderEntity>>

    @Delete
    suspend fun delete(reminder: ReminderEntity)
}
