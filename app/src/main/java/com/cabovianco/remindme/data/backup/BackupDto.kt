package com.cabovianco.remindme.data.backup

import com.cabovianco.remindme.domain.model.ReminderPriority
import com.cabovianco.remindme.domain.model.ReminderRepeat
import com.cabovianco.remindme.domain.model.TagColor
import com.cabovianco.remindme.domain.model.TagIcon
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

const val CURRENT_BACKUP_VERSION = 1

@Serializable
data class BackupDataDto(
    val version: Int = CURRENT_BACKUP_VERSION,
    val exportedAt: String,
    val tags: List<BackupTagDto>,
    val reminders: List<BackupReminderDto>
)

@Serializable
data class BackupTagDto(
    val id: Long,
    val name: String,
    val color: TagColor,
    val icon: TagIcon? = null
)

@Serializable
data class BackupReminderDto(
    val id: Long,
    val title: String,
    val description: String? = null,
    @Serializable(with = ZonedDateTimeSerializer::class)
    val dateTime: ZonedDateTime,
    val repeat: ReminderRepeat,
    val priority: ReminderPriority? = null,
    val tagIds: List<Long> = emptyList()
)

object ZonedDateTimeSerializer : KSerializer<ZonedDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ZonedDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ZonedDateTime) {
        encoder.encodeString(value.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
    }

    override fun deserialize(decoder: Decoder): ZonedDateTime {
        return ZonedDateTime.parse(decoder.decodeString(), DateTimeFormatter.ISO_ZONED_DATE_TIME)
    }
}
