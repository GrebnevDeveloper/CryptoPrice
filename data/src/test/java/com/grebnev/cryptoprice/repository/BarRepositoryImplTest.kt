package com.grebnev.cryptoprice.repository

import app.cash.turbine.test
import com.grebnev.core.wrappers.ErrorType
import com.grebnev.core.wrappers.ResultStatus
import com.grebnev.cryptoprice.data.mapper.BarMapper
import com.grebnev.cryptoprice.data.network.ApiService
import com.grebnev.cryptoprice.data.network.model.bars.BarContainerDto
import com.grebnev.cryptoprice.data.repository.BarRepositoryImpl
import com.grebnev.cryptoprice.domain.entity.Bar
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class BarRepositoryImplTest {
    @MockK
    private lateinit var apiService: ApiService

    @MockK
    private lateinit var mapper: BarMapper
    private lateinit var repository: BarRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = BarRepositoryImpl(apiService, mapper)
    }

    @Test
    fun `getBarsForCoin should emit Success with bars when API call succeeds`() =
        runTest {
            val fromSymbol = "BTC"
            val timeFrame = "day"
            val barContainerDto = mockk<BarContainerDto>()
            val expectedBars = listOf(mockk<Bar>())

            coEvery { apiService.getBarsForCoin(timeFrame = timeFrame, fSyms = fromSymbol) } returns
                barContainerDto
            every { mapper.mapBarContainerDtoToBarEntity(barContainerDto) } returns expectedBars

            repository.getBarsForCoin(fromSymbol, timeFrame).test {
                val result = awaitItem()

                assertTrue(result is ResultStatus.Success)
                assertEquals(expectedBars, (result as ResultStatus.Success).data)

                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()

            coVerify { apiService.getBarsForCoin(timeFrame = timeFrame, fSyms = fromSymbol) }
            verify { mapper.mapBarContainerDtoToBarEntity(barContainerDto) }
        }

    @Test
    fun `getBarsForCoin should emit Error when API call fails`() =
        runTest {
            val fromSymbol = "BTC"
            val timeFrame = "day"
            val expectedError = ErrorType.NETWORK_ERROR

            coEvery { apiService.getBarsForCoin(timeFrame = timeFrame, fSyms = fromSymbol) } throws
                IOException()

            repository.getBarsForCoin(fromSymbol, timeFrame).test(timeout = 13.seconds) {
                val result = awaitItem()

                assertTrue(result is ResultStatus.Error)
                assertEquals(expectedError, (result as ResultStatus.Error).error)

                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()

            coVerify(exactly = 4) {
                apiService.getBarsForCoin(timeFrame = timeFrame, fSyms = fromSymbol)
            }
        }

    @Test
    fun `getBarsForCoin should retry when API call fails`() =
        runTest {
            val fromSymbol = "BTC"
            val timeFrame = "day"
            val barContainerDto = mockk<BarContainerDto>()
            val expectedBars = listOf(mockk<Bar>())

            coEvery { apiService.getBarsForCoin(timeFrame = timeFrame, fSyms = fromSymbol) }
                .throws(IOException())
                .andThenThrows(IOException())
                .andThen(barContainerDto)

            every { mapper.mapBarContainerDtoToBarEntity(barContainerDto) } returns expectedBars

            repository.getBarsForCoin(fromSymbol, timeFrame).test(timeout = 13.seconds) {
                val result = awaitItem()

                assertTrue(result is ResultStatus.Success)
                assertEquals(expectedBars, (result as ResultStatus.Success).data)

                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()

            coVerify(exactly = 3) {
                apiService.getBarsForCoin(timeFrame = timeFrame, fSyms = fromSymbol)
            }
        }
}