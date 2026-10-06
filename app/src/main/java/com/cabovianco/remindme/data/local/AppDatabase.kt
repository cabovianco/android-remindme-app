package com.cabovianco.remindme.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cabovianco.remindme.data.local.converter.reminder.ReminderPriorityConverter
import com.cabovianco.remindme.data.local.converter.reminder.ReminderRepeatConverter
import com.cabovianco.remindme.data.local.converter.reminder.ZonedDateTimeConverter
import com.cabovianco.remindme.data.local.converter.tag.TagColorConverter
import com.cabovianco.remindme.data.local.converter.tag.TagIconConverter
import com.cabovianco.remindme.data.local.converter.tag.TagListConverter
import com.cabovianco.remindme.data.local.dao.ReminderDao
import com.cabovianco.remindme.data.local.dao.ReminderHistoryDao
import com.cabovianco.remindme.data.local.dao.TagDao
import com.cabovianco.remindme.data.local.entity.ReminderEntity
import com.cabovianco.remindme.data.local.entity.ReminderHistoryEntryEntity
import com.cabovianco.remindme.data.local.entity.ReminderTagCrossRef
import com.cabovianco.remindme.data.local.entity.TagEntity

@Database(
    entities = [
        ReminderEntity::class,
        TagEntity::class,
        ReminderTagCrossRef::class,
        ReminderHistoryEntryEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(
    ZonedDateTimeConverter::class,
    ReminderRepeatConverter::class,
    ReminderPriorityConverter::class,
    TagColorConverter::class,
    TagIconConverter::class,
    TagListConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
    abstract fun tagDao(): TagDao
    abstract fun reminderHistoryDao(): ReminderHistoryDao
}
