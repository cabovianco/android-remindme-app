package com.cabovianco.remindme.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cabovianco.remindme.data.local.converter.ReminderRepeatConverter
import com.cabovianco.remindme.data.local.converter.TagColorConverter
import com.cabovianco.remindme.data.local.converter.TagIconConverter
import com.cabovianco.remindme.data.local.converter.ZonedDateTimeConverter
import com.cabovianco.remindme.data.local.dao.ReminderDao
import com.cabovianco.remindme.data.local.dao.TagDao
import com.cabovianco.remindme.data.local.entity.ReminderEntity
import com.cabovianco.remindme.data.local.entity.ReminderTagCrossRef
import com.cabovianco.remindme.data.local.entity.TagEntity

@Database(
    entities = [
        ReminderEntity::class,
        TagEntity::class,
        ReminderTagCrossRef::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(
    ZonedDateTimeConverter::class,
    ReminderRepeatConverter::class,
    TagColorConverter::class,
    TagIconConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
    abstract fun tagDao(): TagDao
}
