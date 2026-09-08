package br.com.mykytadu.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class PartialDateTest {
    @Test
    fun `deve preservar data completa`() {
        val date = PartialDate(2024, 2, 29)
        assertEquals(2024, date.year)
        assertEquals(2, date.month)
        assertEquals(29, date.day)
    }

    @Test
    fun `deve aceitar todas as combinacoes de componentes ausentes`() {
        for (year in listOf(null, 2024)) {
            for (month in listOf(null, 2)) {
                for (day in listOf(null, 29)) {
                    val date = PartialDate(year, month, day)
                    assertEquals(year, date.year)
                    assertEquals(month, date.month)
                    assertEquals(day, date.day)
                }
            }
        }
    }

    @Test
    fun `deve aceitar limites sem inferir calendario ou ano`() {
        assertEquals(1, PartialDate(month = 1, day = 1).day)
        assertEquals(31, PartialDate(month = 12, day = 31).day)
        assertEquals(31, PartialDate(month = 2, day = 31).day)
        assertNull(PartialDate(month = 2, day = 29).year)
        assertEquals(0, PartialDate(year = 0).year)
    }

    @Test
    fun `deve rejeitar somente meses fora do intervalo`() {
        for (month in listOf(Int.MIN_VALUE, -1, 0, 13, Int.MAX_VALUE)) {
            assertFailsWith<IllegalArgumentException> { PartialDate(month = month) }
        }
    }

    @Test
    fun `deve rejeitar somente dias fora do intervalo`() {
        for (day in listOf(Int.MIN_VALUE, -1, 0, 32, Int.MAX_VALUE)) {
            assertFailsWith<IllegalArgumentException> { PartialDate(day = day) }
        }
    }
}
