package com.grebnev.cryptoprice.mapper

import com.grebnev.core.extensions.convertTimestampToTimeByPattern
import com.grebnev.cryptoprice.data.mapper.NewsMapper
import com.grebnev.cryptoprice.data.network.model.news.NewsDto
import com.grebnev.cryptoprice.data.network.model.news.NewsResponseDto
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.TimeZone

class NewsMapperTest {
    private lateinit var mapper: NewsMapper

    @Before
    fun setUp() {
        mapper = NewsMapper()
    }

    @Test
    fun `mapNewsResponseToNewsEntity should convert NewsResponseDto to list of News correctly`() {
        val timestamp = 1672531200L
        val timeZone = TimeZone.getTimeZone("UTC")
        val pattern = "dd.MM.yyyy HH:mm"
        val expectedFormattedDate = "01.01.2023 04:00"

        mockkStatic("com.grebnev.core.extensions.LongExKt")
        every {
            any<Long>().convertTimestampToTimeByPattern(
                pattern = eq(pattern),
                timeZone = timeZone,
            )
        } returns expectedFormattedDate

        val newsDto =
            listOf(
                NewsDto(
                    id = 123,
                    publishedOn = timestamp,
                    imageUrl = "https://example.com/image.jpg",
                    sourceUrl = "https://example.com/news",
                    title = "Test News Title",
                ),
            )
        val newsResponseDto = NewsResponseDto(responseData = newsDto)

        val result = mapper.mapNewsResponseToNewsEntity(newsResponseDto)

        assertEquals(1, result.size)
        assertEquals(123, result[0].id)
        assertEquals(expectedFormattedDate, result[0].publishedOn)
        assertEquals("https://example.com/image.jpg", result[0].imageUrl)
        assertEquals("https://example.com/news", result[0].sourceUrl)
        assertEquals("Test News Title", result[0].title)

        unmockkStatic("com.grebnev.core.extensions.LongExKt")
    }

    @Test
    fun `mapNewsResponseToNewsEntity should return empty list for empty news list`() {
        val newsResponseDto = NewsResponseDto(responseData = emptyList())

        val result = mapper.mapNewsResponseToNewsEntity(newsResponseDto)

        assertTrue(result.isEmpty())
    }
}