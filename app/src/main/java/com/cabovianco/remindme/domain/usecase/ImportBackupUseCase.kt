package com.cabovianco.remindme.domain.usecase

import android.net.Uri
import com.cabovianco.remindme.data.alarm.AlarmScheduler
import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.model.Tag
import com.cabovianco.remindme.domain.repository.BackupRepository
import com.cabovianco.remindme.domain.repository.ReminderRepository
import com.cabovianco.remindme.domain.repository.TagRepository
import kotlinx.coroutines.flow.first
import java.time.ZonedDateTime
import javax.inject.Inject

const val CURRENT_BACKUP_VERSION = 1

class ImportBackupUseCase @Inject constructor(
    private val tagRepository: TagRepository,
    private val reminderRepository: ReminderRepository,
    private val backupRepository: BackupRepository,
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke(uri: Uri): Result<Unit> = try {
        val backupData = backupRepository.readBackup(uri).getOrElse {
            return Result.failure(it)
        }

        if (backupData.version > CURRENT_BACKUP_VERSION) {
            return Result.failure(IllegalStateException("Unsupported backup version: ${backupData.version}."))
        }

        val existingTags = tagRepository.getAll().first()
        val tagIdMap = mutableMapOf<Long, Long>()
        val tagObjectsMap = mutableMapOf<Long, Tag>()

        existingTags.forEach { tag ->
            tagObjectsMap[tag.id] = tag
        }

        for (backupTag in backupData.tags) {
            val existingTag =
                existingTags.find { it.name.equals(backupTag.name, ignoreCase = true) }

            if (existingTag != null) {
                tagIdMap[backupTag.id] = existingTag.id

            } else {
                val tag = Tag(
                    id = 0,
                    name = backupTag.name,
                    color = backupTag.color,
                    icon = backupTag.icon
                )

                tagRepository.insert(tag)
                    .onSuccess { id ->
                        tagIdMap[backupTag.id] = id
                        tagObjectsMap[id] = tag.copy(id = id)
                    }
            }
        }

        val reminders = backupData.reminders.filter {
            it.dateTime.isAfter(
                ZonedDateTime.now()
                    .withSecond(0)
                    .withNano(0)
            )
        }

        for (backupReminder in reminders) {
            val tags = backupReminder.tagIds.mapNotNull { id ->
                tagIdMap[id]?.let { tagObjectsMap[it] }
            }

            val reminder = Reminder(
                id = 0,
                title = backupReminder.title,
                description = backupReminder.description,
                dateTime = backupReminder.dateTime,
                repeat = backupReminder.repeat,
                priority = backupReminder.priority,
                tags = tags
            )

            reminderRepository.insert(reminder)
                .onSuccess { id ->
                    alarmScheduler.schedule(reminder.copy(id = id))
                }
        }

        Result.success(Unit)

    } catch (ex: Exception) {
        Result.failure(ex)
    }
}
