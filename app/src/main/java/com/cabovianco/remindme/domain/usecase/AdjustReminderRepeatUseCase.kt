package com.cabovianco.remindme.domain.usecase

import com.cabovianco.remindme.domain.model.ReminderRepeat
import java.time.ZonedDateTime
import javax.inject.Inject

class AdjustReminderRepeatUseCase @Inject constructor() {
    operator fun invoke(
        currentRepeat: ReminderRepeat,
        newRepeat: ReminderRepeat,
        reminderDateTime: ZonedDateTime
    ): ReminderRepeat {
        val currentInterval = currentRepeat.interval
        var repeat = newRepeat.copyWith(currentInterval)

        if (repeat is ReminderRepeat.Weekly && repeat.days.isEmpty()) {
            val dayOfReminder = reminderDateTime.dayOfWeek
            repeat = repeat.copy(days = setOf(dayOfReminder))
        }

        return repeat
    }
}
