package com.grebnev.cryptoprice.extensions

import com.grebnev.core.extensions.formatWithRoundAndDelimiter
import com.grebnev.core.extensions.formatWithRoundAndSuffix
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Locale

class DoubleExTest {
    @Before
    fun setUp() {
        Locale.setDefault(Locale.ENGLISH)
    }

    @Test
    fun `formatWithRoundAndDelimiter should format with default 2 decimal places`() {
        val number = 1234.5678

        val result = number.formatWithRoundAndDelimiter()

        assertEquals("1,234.57", result)
    }

    @Test
    fun `formatWithRoundAndDelimiter should format with specified decimal places`() {
        val number = 1234.5678

        val result1 = number.formatWithRoundAndDelimiter(0)
        val result2 = number.formatWithRoundAndDelimiter(4)

        assertEquals("1,235", result1)
        assertEquals("1,234.5678", result2)
    }

    @Test
    fun `formatWithRoundAndDelimiter should handle negative numbers`() {
        val number = -1234.5678

        val result = number.formatWithRoundAndDelimiter()

        assertEquals("-1,234.57", result)
    }

    @Test
    fun `formatWithRoundAndDelimiter should handle zero`() {
        val number = 0.0

        val result = number.formatWithRoundAndDelimiter()

        assertEquals("0.00", result)
    }

    @Test
    fun `formatWithRoundAndSuffix should format billions with B suffix`() {
        val number1 = 1_234_567_890.0
        val number2 = -2_500_000_000.0

        val result1 = number1.formatWithRoundAndSuffix()
        val result2 = number2.formatWithRoundAndSuffix()

        assertEquals("1.23 B", result1)
        assertEquals("-2.5 B", result2)
    }

    @Test
    fun `formatWithRoundAndSuffix should format millions with M suffix`() {
        val number1 = 12_345_678.0
        val number2 = -50_000_000.0

        val result1 = number1.formatWithRoundAndSuffix()
        val result2 = number2.formatWithRoundAndSuffix()

        assertEquals("12.35 M", result1)
        assertEquals("-50 M", result2)
    }

    @Test
    fun `formatWithRoundAndSuffix should format thousands with K suffix`() {
        val number1 = 12_345.0
        val number2 = -5_678.0

        val result1 = number1.formatWithRoundAndSuffix()
        val result2 = number2.formatWithRoundAndSuffix()

        assertEquals("12.35 K", result1)
        assertEquals("-5.68 K", result2)
    }

    @Test
    fun `formatWithRoundAndSuffix should format small numbers without suffix`() {
        val number1 = 12.345
        val number2 = -6.789

        val result1 = number1.formatWithRoundAndSuffix()
        val result2 = number2.formatWithRoundAndSuffix()

        assertEquals("12.35", result1)
        assertEquals("-6.79", result2)
    }

    @Test
    fun `formatWithRoundAndSuffix should handle zero`() {
        val number = 0.0

        val result = number.formatWithRoundAndSuffix()

        assertEquals("0", result)
    }

    @Test
    fun `formatWithRoundAndSuffix should handle edge cases`() {
        val number1 = 999.999
        val number2 = 1_000.0
        val number3 = 999_999.0
        val number4 = 1_000_000.0
        val number5 = 999_999_999.0
        val number6 = 1_000_000_000.0

        val result1 = number1.formatWithRoundAndSuffix()
        val result2 = number2.formatWithRoundAndSuffix()
        val result3 = number3.formatWithRoundAndSuffix()
        val result4 = number4.formatWithRoundAndSuffix()
        val result5 = number5.formatWithRoundAndSuffix()
        val result6 = number6.formatWithRoundAndSuffix()

        assertEquals("1,000", result1)
        assertEquals("1 K", result2)
        assertEquals("1,000 K", result3)
        assertEquals("1 M", result4)
        assertEquals("1,000 M", result5)
        assertEquals("1 B", result6)
    }
}