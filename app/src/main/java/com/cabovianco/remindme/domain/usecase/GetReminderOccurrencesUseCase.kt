package com.cabovianco.remindme.domain.usecase

import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.model.ReminderRepeat
import java.time.ZonedDateTime
import javax.inject.Inject

class GetReminderOccurrencesUseCase @Inject constructor() {
    operator fun invoke(
        reminders: List<Reminder>,
        date: ZonedDateTime
    ): List<Reminder> {
        val occurrences = mutableListOf<Reminder>()

        val startOfDay = date.withHour(0).withMinute(0).withSecond(0).withNano(0)
        val endOfDay = startOfDay.plusDays(1).minusNanos(1)

        reminders.forEach {
            var occurrenceDate = it.dateTime

            while (occurrenceDate.isBefore(startOfDay) && it.repeat != ReminderRepeat.Never) {
                occurrenceDate = it.repeat.next(occurrenceDate)
            }

            if (!occurrenceDate.isBefore(startOfDay) && !occurrenceDate.isAfter(endOfDay)) {
                occurrences.add(it.copy(dateTime = occurrenceDate))
            }
        }

        return occurrences.sortedBy { it.dateTime }
    }
}
