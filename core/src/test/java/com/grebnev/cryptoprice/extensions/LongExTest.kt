package com.grebnev.cryptoprice.extensions

import com.grebnev.core.extensions.convertTimestampToTimeByPattern
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class LongExTest {
    @Test
    fun `convertTimestampToTimeByPattern should return empty string for null input`() {
        val nullTimestamp: Long? = null

        val result = nullTimestamp.convertTimestampToTimeByPattern("HH:mm:ss")

        assertEquals("", result)
    }

    @Test
    fun `convertTimestampToTimeByPattern should format timestamp correctly with default timezone`() {
        val timestamp = 1672531200L

        val result = timestamp.convertTimestampToTimeByPattern("yyyy-MM-dd HH:mm:ss")

        val expected =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .apply {
                    timeZone = TimeZone.getDefault()
                }.format(Date(timestamp * 1000))

        assertEquals(expected, result)
    }

    @Test
    fun `convertTimestampToTimeByPattern should respect specified timezone`() {
        val timestamp = 1672531200L
        val specifiedTimeZone = TimeZone.getTimeZone("America/New_York")

        val result =
            timestamp.convertTimestampToTimeByPattern(
                "yyyy-MM-dd HH:mm:ss",
                specifiedTimeZone,
            )

        val expected =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .apply {
                    timeZone = specifiedTimeZone
                }.format(Date(timestamp * 1000))

        assertEquals(expected, result)
    }

    @Test
    fun `convertTimestampToTimeByPattern should handle different patterns`() {
        val timestamp = 1672531200L

        val utcTimeZone = TimeZone.getTimeZone("UTC")

        assertEquals("2023", timestamp.convertTimestampToTimeByPattern("yyyy", utcTimeZone))
        assertEquals("01-01", timestamp.convertTimestampToTimeByPattern("MM-dd", utcTimeZone))
        assertEquals("00:00", timestamp.convertTimestampToTimeByPattern("HH:mm", utcTimeZone))
    }

    @Test
    fun `convertTimestampToTimeByPattern should use device locale`() {
        val timestamp = 1672531200L
        val originalLocale = Locale.getDefault()

        try {
            Locale.setDefault(Locale.FRANCE)

            val result = timestamp.convertTimestampToTimeByPattern("EEEE, d MMMM yyyy")

            val expected =
                SimpleDateFormat("EEEE, d MMMM yyyy", Locale.FRANCE)
                    .format(Date(timestamp * 1000))

            assertEquals(expected, result)
        } finally {
            Locale.setDefault(originalLocale)
        }
    }

    @Test
    fun `convertTimestampToTimeByPattern should handle edge cases`() {
        val zeroTimestamp = 0L

        assertEquals("1970-01-01", zeroTimestamp.convertTimestampToTimeByPattern("yyyy-MM-dd"))

        val maxTimestamp = Long.MAX_VALUE / 1000
        assertTrue(maxTimestamp.convertTimestampToTimeByPattern("yyyy").isNotEmpty())
    }
}