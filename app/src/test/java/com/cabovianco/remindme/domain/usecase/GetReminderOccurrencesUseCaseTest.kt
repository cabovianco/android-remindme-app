package com.cabovianco.remindme.domain.usecase

import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.model.ReminderRepeat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.ZonedDateTime

class GetReminderOccurrencesUseCaseTest {
    private val useCase = GetReminderOccurrencesUseCase()

    private val baseDate = ZonedDateTime.now()
        .withHour(10)
        .withMinute(0)
        .withSecond(0)
        .withNano(0)

    @Test
    fun `when reminder is today and does not repeat, it should be returned`() {
        val reminder = createReminder(dateTime = baseDate, repeat = ReminderRepeat.Never)
        val result = useCase(listOf(reminder), baseDate)

        assertEquals(1, result.size)
        assertEquals(reminder, result[0])
    }

    @Test
    fun `when reminder is not today and does not repeat, it should not be returned`() {
        val reminder = createReminder(dateTime = baseDate.plusDays(1), repeat = ReminderRepeat.Never)
        val result = useCase(listOf(reminder), baseDate)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `when reminder is from yesterday and repeats daily, it should be returned for today`() {
        val reminder = createReminder(dateTime = baseDate.minusDays(1), repeat = ReminderRepeat.Daily(1))
        val result = useCase(listOf(reminder), baseDate)

        assertEquals(1, result.size)
        assertEquals(baseDate, result[0].dateTime)
    }

    @Test
    fun `when reminder repeats weekly on specific days, it should be returned only on those days`() {
        val monday = baseDate.with(DayOfWeek.MONDAY)
        val tuesday = baseDate.with(DayOfWeek.TUESDAY)
        val reminder = createReminder(
            dateTime = monday,
            repeat = ReminderRepeat.Weekly(interval = 1, days = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
        )

        val mondayResult = useCase(listOf(reminder), monday)
        assertEquals(1, mondayResult.size)
        assertEquals(monday, mondayResult[0].dateTime)

        val tuesdayResult = useCase(listOf(reminder), tuesday)
        assertTrue(tuesdayResult.isEmpty())

        val wednesday = baseDate.with(DayOfWeek.WEDNESDAY)
        val wednesdayResult = useCase(listOf(reminder), wednesday)
        assertEquals(1, wednesdayResult.size)
        assertEquals(wednesday, wednesdayResult[0].dateTime)
    }

    @Test
    fun `when reminder repeats monthly, it should be returned on the same day next month`() {
        val reminder = createReminder(dateTime = baseDate, repeat = ReminderRepeat.Monthly(1))
        val nextMonth = baseDate.plusMonths(1)

        val result = useCase(listOf(reminder), nextMonth)

        assertEquals(1, result.size)
        assertEquals(nextMonth, result[0].dateTime)
    }

    @Test
    fun `reminders should be sorted by time`() {
        val r1 = createReminder(dateTime = baseDate.withHour(12), repeat = ReminderRepeat.Never)
        val r2 = createReminder(dateTime = baseDate.withHour(8), repeat = ReminderRepeat.Never)

        val result = useCase(listOf(r1, r2), baseDate)

        assertEquals(2, result.size)
        assertEquals(8, result[0].dateTime.hour)
        assertEquals(12, result[1].dateTime.hour)
    }

    private fun createReminder(
        id: Long = 1,
        title: String = "Test",
        dateTime: ZonedDateTime,
        repeat: ReminderRepeat
    ) = Reminder(
        id = id,
        title = title,
        description = null,
        dateTime = dateTime,
        repeat = repeat,
        tags = emptyList()
    )
}
