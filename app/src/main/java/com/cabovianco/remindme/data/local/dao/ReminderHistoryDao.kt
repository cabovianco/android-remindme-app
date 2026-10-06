package com.cabovianco.remindme.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cabovianco.remindme.data.local.entity.ReminderHistoryEntryEntity
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

@Dao
interface ReminderHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ReminderHistoryEntryEntity): Long

    @Query("SELECT * FROM reminder_entries ORDER BY triggeredAt DESC")
    fun getAll(): Flow<List<ReminderHistoryEntryEntity>>

    @Delete
    suspend fun delete(entity: ReminderHistoryEntryEntity)

    @Query("DELETE FROM reminder_entries WHERE triggeredAt < :cutoffDate")
    suspend fun deleteOlderThan(cutoffDate: ZonedDateTime)

    @Query("DELETE FROM reminder_entries")
    suspend fun deleteAll()
}
