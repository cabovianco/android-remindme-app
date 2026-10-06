package com.cabovianco.remindme.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `reminder_entries` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `reminderId` INTEGER NOT NULL,
                `title` TEXT NOT NULL,
                `description` TEXT,
                `triggeredAt` TEXT NOT NULL,
                `priority` TEXT,
                `tags` TEXT NOT NULL DEFAULT '[]'
            )
            """.trimIndent()
        )
    }
}
