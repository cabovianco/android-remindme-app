package com.cabovianco.remindme.domain.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.DayOfWeek
import java.time.ZonedDateTime

@Serializable
sealed interface ReminderRepeat {
    val interval: Int

    fun next(from: ZonedDateTime): ZonedDateTime

    fun copyWith(interval: Int): ReminderRepeat = when (this) {
        is Never -> this
        is Daily -> copy(interval = interval)
        is Weekly -> copy(interval = interval)
        is Monthly -> copy(interval = interval)
        is Yearly -> copy(interval = interval)
    }

    @Serializable
    @SerialName("never")
    data object Never : ReminderRepeat {
        override val interval: Int = 1
        override fun next(from: ZonedDateTime): ZonedDateTime = from
    }

    @Serializable
    @SerialName("daily")
    data class Daily(override val interval: Int = 1) : ReminderRepeat {
        override fun next(from: ZonedDateTime): ZonedDateTime = from.plusDays(interval.toLong())
    }

    @Serializable
    @SerialName("weekly")
    data class Weekly(
        override val interval: Int = 1,
        val days: Set<@Serializable(with = DayOfWeekSerializer::class) DayOfWeek> = emptySet()
    ) : ReminderRepeat {
        override fun next(from: ZonedDateTime): ZonedDateTime {
            if (days.isEmpty()) return from.plusWeeks(interval.toLong())

            val sortedDays = days.sorted()
            val currentDay = from.dayOfWeek

            val nextDay = sortedDays.firstOrNull { it > currentDay }

            return if (nextDay != null) {
                from.plusDays((nextDay.value - currentDay.value).toLong())

            } else {
                from.plusWeeks(interval.toLong())
                    .with(sortedDays.first())
            }
        }
    }

    @Serializable
    @SerialName("monthly")
    data class Monthly(override val interval: Int = 1) : ReminderRepeat {
        override fun next(from: ZonedDateTime): ZonedDateTime = from.plusMonths(interval.toLong())
    }

    @Serializable
    @SerialName("yearly")
    data class Yearly(override val interval: Int = 1) : ReminderRepeat {
        override fun next(from: ZonedDateTime): ZonedDateTime = from.plusYears(interval.toLong())
    }
}

object DayOfWeekSerializer : KSerializer<DayOfWeek> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("DayOfWeek", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: DayOfWeek) = encoder.encodeInt(value.value)

    override fun deserialize(decoder: Decoder): DayOfWeek = DayOfWeek.of(decoder.decodeInt())
}
