package com.cabovianco.remindme.domain.repository

import android.net.Uri
import com.cabovianco.remindme.domain.model.BackupData
import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.model.Tag

interface BackupRepository {
    suspend fun exportBackup(uri: Uri, reminders: List<Reminder>, tags: List<Tag>): Result<Unit>
    suspend fun readBackup(uri: Uri): Result<BackupData>
}
