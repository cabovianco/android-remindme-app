package com.cabovianco.remindme.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("UPDATE reminders SET repeat = '{\"type\":\"never\"}' WHERE repeat = 'NEVER'")
        db.execSQL("UPDATE reminders SET repeat = '{\"type\":\"daily\",\"interval\":1}' WHERE repeat = 'DAILY'")
        db.execSQL(
            """
            UPDATE reminders 
            SET repeat = '{"type":"weekly","interval":1,"days":[' || 
                (CASE strftime('%w', substr(dateTime, 1, 10)) 
                    WHEN '0' THEN '7' 
                    ELSE strftime('%w', substr(dateTime, 1, 10)) 
                END) || ']}' 
            WHERE repeat = 'WEEKLY'
            """.trimIndent()
        )
        db.execSQL("UPDATE reminders SET repeat = '{\"type\":\"monthly\",\"interval\":1}' WHERE repeat = 'MONTHLY'")
        db.execSQL("UPDATE reminders SET repeat = '{\"type\":\"yearly\",\"interval\":1}' WHERE repeat = 'YEARLY'")

        db.execSQL("CREATE TABLE IF NOT EXISTS `tags` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `color` TEXT NOT NULL, `icon` TEXT)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `reminder_tag` (`reminderId` INTEGER NOT NULL, `tagId` INTEGER NOT NULL, PRIMARY KEY(`reminderId`, `tagId`), FOREIGN KEY(`reminderId`) REFERENCES `reminders`(`id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`tagId`) REFERENCES `tags`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")

        db.execSQL("DELETE FROM reminders WHERE repeat = '{\"type\":\"never\"}' AND substr(dateTime, 1, 10) < date('now', '-2 days')")
    }
}
