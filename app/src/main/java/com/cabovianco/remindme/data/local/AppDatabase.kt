package com.cabovianco.remindme.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cabovianco.remindme.data.local.converter.ZonedDateTimeConverter
import com.cabovianco.remindme.data.local.dao.ReminderDao
import com.cabovianco.remindme.data.local.entity.ReminderEntity

@Database(entities = [ReminderEntity::class], version = 1, exportSchema = false)
@TypeConverters(ZonedDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
}
