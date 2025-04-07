package com.grebnev.cryptoprice.usecase

import app.cash.turbine.test
import com.grebnev.core.wrappers.ErrorType
import com.grebnev.core.wrappers.ResultStatus
import com.grebnev.cryptoprice.domain.entity.Bar
import com.grebnev.cryptoprice.domain.repository.BarRepository
import com.grebnev.cryptoprice.domain.usecase.GetBarsForCoinUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetBarsForCoinUseCaseTest {
    @MockK
    private lateinit var repository: BarRepository

    private lateinit var useCase: GetBarsForCoinUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = GetBarsForCoinUseCase(repository)
    }

    @Test
    fun `invoke should return bars from repository`() =
        runTest {
            val mockBars = listOf(mockk<Bar>())
            val timeFrame = "histoday"
            val symbol = "BTC"
            coEvery { repository.getBarsForCoin(symbol, timeFrame) } returns
                flowOf(ResultStatus.Success(mockBars))

            val resultFlow = useCase(timeFrame, symbol)

            resultFlow.test {
                val result = awaitItem()
                assertTrue(result is ResultStatus.Success)
                assertEquals(mockBars, (result as ResultStatus.Success).data)
                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()

            coVerify { repository.getBarsForCoin(symbol, timeFrame) }
        }

    @Test
    fun `invoke should return error when repository fails`() =
        runTest {
            val timeFrame = "histoday"
            val symbol = "BTC"
            val error = ErrorType.NETWORK_ERROR
            coEvery { repository.getBarsForCoin(symbol, timeFrame) } returns flowOf(ResultStatus.Error(error))

            val resultFlow = useCase(timeFrame, symbol)

            resultFlow.test {
                val result = awaitItem()
                assertTrue(result is ResultStatus.Error)
                assertEquals(error, (result as ResultStatus.Error).error)
                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()
        }
}