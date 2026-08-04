package com.cabovianco.remindme.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZonedDateTime

class GetSelectableDatesUseCaseTest {
    private val useCase = GetSelectableDatesUseCase()

    @Test
    fun `when range is 7 days, it should return 7 dates`() {
        val from = ZonedDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0)
        val to = from.plusDays(6)

        val result = useCase(from, to)

        assertEquals(7, result.size)
        assertEquals(from, result[0])
        assertEquals(to, result[6])
    }

    @Test
    fun `when from is after to, it should return empty list`() {
        val from = ZonedDateTime.now()
        val to = from.minusDays(1)

        val result = useCase(from, to)

        assertEquals(0, result.size)
    }
}
