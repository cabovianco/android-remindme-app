package com.cabovianco.remindme.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.cabovianco.remindme.data.backup.BackupDataDto
import com.cabovianco.remindme.data.backup.BackupReminderDto
import com.cabovianco.remindme.data.backup.BackupTagDto
import com.cabovianco.remindme.data.backup.CURRENT_BACKUP_VERSION
import com.cabovianco.remindme.domain.model.BackupData
import com.cabovianco.remindme.domain.model.BackupReminder
import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.model.Tag
import com.cabovianco.remindme.domain.repository.BackupRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private const val TAG = "BackupRepository"

class BackupRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : BackupRepository {

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    override suspend fun exportBackup(
        uri: Uri,
        reminders: List<Reminder>,
        tags: List<Tag>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val backupData = BackupDataDto(
                version = CURRENT_BACKUP_VERSION,
                exportedAt = ZonedDateTime
                    .now()
                    .format(DateTimeFormatter.ISO_ZONED_DATE_TIME),
                tags = tags.map { tag ->
                    BackupTagDto(
                        id = tag.id,
                        name = tag.name,
                        color = tag.color,
                        icon = tag.icon
                    )
                },
                reminders = reminders.map { reminder ->
                    BackupReminderDto(
                        id = reminder.id,
                        title = reminder.title,
                        description = reminder.description,
                        dateTime = reminder.dateTime,
                        repeat = reminder.repeat,
                        priority = reminder.priority,
                        tagIds = reminder.tags.map { it.id }
                    )
                }
            )

            val jsonString = json.encodeToString(BackupDataDto.serializer(), backupData)

            context.contentResolver.openOutputStream(uri, "rwt")?.use { outputStream ->
                outputStream.write(jsonString.toByteArray(Charsets.UTF_8))
            } ?: throw IllegalStateException("Could not open output stream for $uri.")

            Result.success(Unit)

        } catch (ex: Exception) {
            Log.e(TAG, "BackupRepository::exportBackup", ex)
            Result.failure(ex)
        }
    }

    override suspend fun readBackup(uri: Uri): Result<BackupData> =
        withContext(Dispatchers.IO) {
            try {
                val jsonContent = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).use { reader ->
                        reader.readText()
                    }
                } ?: throw IllegalStateException("Could not open input stream for $uri.")

                val backupDataDto = try {
                    json.decodeFromString(BackupDataDto.serializer(), jsonContent)

                } catch (ex: Exception) {
                    return@withContext Result.failure(
                        IllegalArgumentException("Invalid backup file format.", ex)
                    )
                }

                Result.success(
                    BackupData(
                        version = backupDataDto.version,
                        exportedAt = backupDataDto.exportedAt,
                        tags = backupDataDto.tags.map { tag ->
                            Tag(
                                id = tag.id,
                                name = tag.name,
                                color = tag.color,
                                icon = tag.icon
                            )
                        },
                        reminders = backupDataDto.reminders.map { reminder ->
                            BackupReminder(
                                id = reminder.id,
                                title = reminder.title,
                                description = reminder.description,
                                dateTime = reminder.dateTime,
                                repeat = reminder.repeat,
                                priority = reminder.priority,
                                tagIds = reminder.tagIds
                            )
                        }
                    )
                )

            } catch (ex: Exception) {
                Log.e(TAG, "BackupRepository::readBackup", ex)
                Result.failure(ex)
            }
        }
}
