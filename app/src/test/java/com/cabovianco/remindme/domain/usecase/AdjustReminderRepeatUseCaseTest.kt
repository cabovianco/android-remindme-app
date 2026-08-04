package com.cabovianco.remindme.domain.usecase

import com.cabovianco.remindme.domain.model.ReminderRepeat
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek
import java.time.ZonedDateTime

class AdjustReminderRepeatUseCaseTest {
    private val useCase = AdjustReminderRepeatUseCase()
    private val dateTime = ZonedDateTime.now()
        .with(DayOfWeek.MONDAY)

    @Test
    fun `when changing to weekly, it should keep the interval`() {
        val current = ReminderRepeat.Daily(interval = 3)
        val newRepeat = ReminderRepeat.Weekly()

        val result = useCase(current, newRepeat, dateTime)

        assertEquals(3, result.interval)
    }

    @Test
    fun `when changing to weekly and days are empty, it should use the reminder day of week`() {
        val current = ReminderRepeat.Never
        val newRepeat = ReminderRepeat.Weekly()

        val result = useCase(current, newRepeat, dateTime) as ReminderRepeat.Weekly

        assertEquals(setOf(DayOfWeek.MONDAY), result.days)
    }

    @Test
    fun `when changing to weekly and days are already set, it should keep them`() {
        val current = ReminderRepeat.Never
        val newRepeat = ReminderRepeat.Weekly(days = setOf(DayOfWeek.FRIDAY))

        val result = useCase(current, newRepeat, dateTime) as ReminderRepeat.Weekly

        assertEquals(setOf(DayOfWeek.FRIDAY), result.days)
    }
}
