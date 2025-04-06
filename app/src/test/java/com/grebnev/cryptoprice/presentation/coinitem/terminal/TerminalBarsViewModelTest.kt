package com.grebnev.cryptoprice.presentation.coinitem.terminal

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.grebnev.core.wrappers.ErrorType
import com.grebnev.core.wrappers.ResultStatus
import com.grebnev.cryptoprice.domain.entity.Bar
import com.grebnev.cryptoprice.domain.usecase.GetBarsForCoinUseCase
import com.grebnev.cryptoprice.presentation.base.error.ErrorMessageProvider
import com.grebnev.cryptoprice.presentation.coinitem.terminal.TerminalBarsScreenState
import com.grebnev.cryptoprice.presentation.coinitem.terminal.TerminalBarsViewModel
import com.grebnev.cryptoprice.presentation.coinitem.terminal.bars.TimeFrame
import com.grebnev.cryptoprice.testutils.observeForTesting
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class TerminalBarsViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var getBarsForCoinUseCase: GetBarsForCoinUseCase

    @MockK
    private lateinit var errorMessageProvider: ErrorMessageProvider

    private lateinit var viewModel: TerminalBarsViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Initial`() =
        runTest {
            viewModel = createViewModel()

            val state = viewModel.barState.observeForTesting()

            Assert.assertTrue(state.values[0] is TerminalBarsScreenState.Initial)
            Assert.assertEquals(TimeFrame.DAILY, viewModel.timeFrame.value)
        }

    @Test
    fun `loadBarsForCoin should emit Loading then Content when success`() =
        runTest {
            val mockBars =
                listOf(
                    mockk<Bar> { every { time } returns 1000L },
                    mockk<Bar> { every { time } returns 2000L },
                )
            val fromSymbol = "BTC"

            coEvery { getBarsForCoinUseCase(any(), fromSymbol) } returns
                flowOf(ResultStatus.Success(mockBars))

            viewModel = createViewModel()
            viewModel.loadBarsForCoin(fromSymbol)

            val state = viewModel.barState.observeForTesting()

            Assert.assertTrue(state.values[0] is TerminalBarsScreenState.Content)
            Assert.assertEquals(
                mockBars.sortedByDescending {
                    it.time
                },
                (state.values[0] as TerminalBarsScreenState.Content).bars,
            )
        }

    @Test
    fun `loadBarsForCoin should emit Loading then Error when failure`() =
        runTest {
            val fromSymbol = "BTC"
            val error = ErrorType.NETWORK_ERROR
            val errorMessage = "Network error"

            coEvery { getBarsForCoinUseCase(any(), fromSymbol) } returns
                flowOf(
                    ResultStatus.Error(
                        error,
                    ),
                )
            coEvery { errorMessageProvider.getErrorMessage(error) } returns errorMessage

            viewModel = createViewModel()
            viewModel.loadBarsForCoin(fromSymbol)

            val state = viewModel.barState.observeForTesting()

            Assert.assertTrue(state.values[0] is TerminalBarsScreenState.Error)
            Assert.assertEquals(
                errorMessage,
                (state.values[0] as TerminalBarsScreenState.Error).message,
            )
        }

    @Test
    fun `changeTimeFrameStatus should update timeFrame and reload data`() =
        runTest {
            val mockBars = listOf(mockk<Bar>())
            val fromSymbol = "BTC"
            val newTimeFrame = TimeFrame.DAILY

            coEvery { getBarsForCoinUseCase(newTimeFrame.value, fromSymbol) } returns
                flowOf(ResultStatus.Success(mockBars))

            viewModel = createViewModel()
            viewModel.changeTimeFrameStatus(newTimeFrame, fromSymbol)

            val state = viewModel.barState.observeForTesting()

            Assert.assertEquals(newTimeFrame, viewModel.timeFrame.value)
            Assert.assertTrue(state.values[0] is TerminalBarsScreenState.Content)
        }

    @Test
    fun `should handle empty bars list`() =
        runTest {
            val fromSymbol = "BTC"
            val emptyBars = emptyList<Bar>()

            coEvery { getBarsForCoinUseCase(any(), fromSymbol) } returns
                flowOf(ResultStatus.Success(emptyBars))

            viewModel = createViewModel()
            viewModel.loadBarsForCoin(fromSymbol)

            val state = viewModel.barState.observeForTesting()

            Assert.assertTrue(state.values[0] is TerminalBarsScreenState.Content)
            Assert.assertTrue((state.values[0] as TerminalBarsScreenState.Content).bars.isEmpty())
        }

    @Test
    fun `should sort bars by time descending`() =
        runTest {
            val unsortedBars =
                listOf(
                    mockk<Bar> { every { time } returns 2000L },
                    mockk<Bar> { every { time } returns 1000L },
                    mockk<Bar> { every { time } returns 3000L },
                )
            val expectedSorted = unsortedBars.sortedByDescending { it.time }
            val fromSymbol = "BTC"

            coEvery { getBarsForCoinUseCase(any(), fromSymbol) } returns
                flowOf(ResultStatus.Success(unsortedBars))

            viewModel = createViewModel()
            viewModel.loadBarsForCoin(fromSymbol)

            val state = viewModel.barState.observeForTesting()

            val contentState = state.values[0] as TerminalBarsScreenState.Content
            Assert.assertEquals(expectedSorted, contentState.bars)
        }

    @Test
    fun `should handle exception in coroutineExceptionHandler`() =
        runTest {
            val fromSymbol = "BTC"
            val exception = IOException("Network error")
            val errorType = ErrorType.NETWORK_ERROR
            val errorMessage = "Network error"

            coEvery { getBarsForCoinUseCase(any(), fromSymbol) } throws exception
            coEvery { errorMessageProvider.getErrorMessage(errorType) } returns errorMessage

            viewModel = createViewModel()
            viewModel.loadBarsForCoin(fromSymbol)

            val state = viewModel.barState.observeForTesting()

            Assert.assertTrue(state.values[0] is TerminalBarsScreenState.Error)
            Assert.assertEquals(
                errorMessage,
                (state.values[0] as TerminalBarsScreenState.Error).message,
            )
        }

    private fun createViewModel(): TerminalBarsViewModel =
        TerminalBarsViewModel(
            getBarsForCoinUseCase,
            errorMessageProvider,
        )
}