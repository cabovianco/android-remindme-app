package com.cabovianco.remindme.domain.usecase

import android.net.Uri
import com.cabovianco.remindme.domain.repository.BackupRepository
import com.cabovianco.remindme.domain.repository.ReminderRepository
import com.cabovianco.remindme.domain.repository.TagRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ExportBackupUseCase @Inject constructor(
    private val tagRepository: TagRepository,
    private val reminderRepository: ReminderRepository,
    private val backupRepository: BackupRepository
) {
    suspend operator fun invoke(uri: Uri): Result<Unit> = try {
        backupRepository.exportBackup(
            uri,
            reminders = reminderRepository.getAll().first(),
            tags = tagRepository.getAll().first()
        )

    } catch (ex: Exception) {
        Result.failure(ex)
    }
}
