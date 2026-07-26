package com.cabovianco.remindme.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.cabovianco.remindme.data.local.entity.ReminderEntity
import com.cabovianco.remindme.data.local.entity.ReminderTagCrossRef
import com.cabovianco.remindme.data.local.entity.ReminderWithTags
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

@Dao
interface ReminderDao {
    @Transaction
    @Query("SELECT * FROM reminders")
    fun getAll(): Flow<List<ReminderWithTags>>

    @Transaction
    @Query("SELECT * FROM reminders WHERE dateTime > :from")
    fun getAllSinceDate(from: ZonedDateTime): Flow<List<ReminderWithTags>>

    @Transaction
    @Query("SELECT * FROM reminders WHERE id = :id")
    fun getById(id: Long): Flow<ReminderWithTags?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: ReminderEntity): Long

    @Update
    suspend fun update(reminder: ReminderEntity)

    @Delete
    suspend fun delete(reminder: ReminderEntity)

    @Transaction
    suspend fun insertWithTags(reminder: ReminderEntity, tagIds: List<Long>): Long {
        val id = insert(reminder)
        insertReminderTagCrossRefs(tagIds.map { ReminderTagCrossRef(id, it) })
        return id
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminderTagCrossRefs(crossRefs: List<ReminderTagCrossRef>)

    @Transaction
    suspend fun updateWithTags(reminder: ReminderEntity, tagIds: List<Long>) {
        update(reminder)
        deleteReminderTagCrossRefs(reminder.id)
        insertReminderTagCrossRefs(tagIds.map { ReminderTagCrossRef(reminder.id, it) })
    }

    @Query("DELETE FROM reminder_tag WHERE reminderId = :reminderId")
    suspend fun deleteReminderTagCrossRefs(reminderId: Long)
}
