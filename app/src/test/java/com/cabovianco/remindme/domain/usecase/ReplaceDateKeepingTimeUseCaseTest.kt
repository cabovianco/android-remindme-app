package com.cabovianco.remindme.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class ReplaceDateKeepingTimeUseCaseTest {
    private val useCase = ReplaceDateKeepingTimeUseCase()

    @Test
    fun `should update only date components and keep time components`() {
        val current = ZonedDateTime.of(2023, 1, 1, 10, 30, 0, 0, ZoneId.systemDefault())
        val newDateMillis = Instant.parse("2024-05-20T00:00:00Z").toEpochMilli()

        val result = useCase(current, newDateMillis)

        assertEquals(2024, result.year)
        assertEquals(5, result.monthValue)
        assertEquals(20, result.dayOfMonth)
        assertEquals(10, result.hour)
        assertEquals(30, result.minute)
    }
}
