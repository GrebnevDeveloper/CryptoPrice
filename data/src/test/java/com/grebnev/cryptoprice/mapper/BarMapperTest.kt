package com.grebnev.cryptoprice.mapper

import com.grebnev.cryptoprice.data.mapper.BarMapper
import com.grebnev.cryptoprice.data.network.model.bars.BarContainerDto
import com.grebnev.cryptoprice.data.network.model.bars.BarDto
import com.grebnev.cryptoprice.data.network.model.bars.BarResponseDto
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test

class BarMapperTest {
    private lateinit var mapper: BarMapper

    @Before
    fun setUp() {
        mapper = BarMapper()
    }

    @Test
    fun `mapBarContainerDtoToBarEntity should convert BarContainerDto to list of Bar correctly`() {
        val barsDto =
            listOf(
                BarDto(
                    high = 51000.0,
                    low = 49000.0,
                    open = 50000.0,
                    close = 50500.0,
                    time = 1672531200L,
                ),
                BarDto(
                    high = 51500.0,
                    low = 49500.0,
                    open = 50500.0,
                    close = 51000.0,
                    time = 1672617600L,
                ),
            )
        val barResponseDto = BarResponseDto(responseData = barsDto)
        val barContainerDto = BarContainerDto(containerData = barResponseDto)

        val result = mapper.mapBarContainerDtoToBarEntity(barContainerDto)

        assertEquals(2, result.size)
        assertEquals(51000.0, result[0].high, 0.0)
        assertEquals(49000.0, result[0].low, 0.0)
        assertEquals(50000.0, result[0].open, 0.0)
        assertEquals(50500.0, result[0].close, 0.0)
        assertEquals(1672531200L, result[0].time)
        assertEquals(51500.0, result[1].high, 0.0)
        assertEquals(49500.0, result[1].low, 0.0)
        assertEquals(50500.0, result[1].open, 0.0)
        assertEquals(51000.0, result[1].close, 0.0)
        assertEquals(1672617600L, result[1].time)
    }

    @Test
    fun `mapBarContainerDtoToBarEntity should return empty list for empty bars list`() {
        val barResponseDto = BarResponseDto(responseData = emptyList())
        val barContainerDto = BarContainerDto(containerData = barResponseDto)

        val result = mapper.mapBarContainerDtoToBarEntity(barContainerDto)

        assertTrue(result.isEmpty())
    }
}