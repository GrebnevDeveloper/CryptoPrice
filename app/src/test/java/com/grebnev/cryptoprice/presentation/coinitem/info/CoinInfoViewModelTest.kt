package com.grebnev.cryptoprice.presentation.coinitem.info

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.grebnev.core.wrappers.ErrorType
import com.grebnev.core.wrappers.ResultStatus
import com.grebnev.cryptoprice.domain.entity.Coin
import com.grebnev.cryptoprice.domain.usecase.GetCoinInfoUseCase
import com.grebnev.cryptoprice.presentation.base.error.ErrorMessageProvider
import com.grebnev.cryptoprice.presentation.coinitem.info.CoinInfoScreenState
import com.grebnev.cryptoprice.presentation.coinitem.info.CoinInfoViewModel
import com.grebnev.cryptoprice.testutils.observeForTesting
import io.mockk.MockKAnnotations
import io.mockk.coEvery
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
class CoinInfoViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var getCoinInfoUseCase: GetCoinInfoUseCase

    @MockK
    private lateinit var errorMessageProvider: ErrorMessageProvider

    private lateinit var viewModel: CoinInfoViewModel
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

            val screenState = viewModel.screenState.observeForTesting()

            Assert.assertTrue(screenState.values[0] is CoinInfoScreenState.Initial)
        }

    @Test
    fun `getCoinInfo should emit Loading then Content when success`() =
        runTest {
            val mockCoin = mockk<Coin>()
            val fromSymbol = "BTC"

            coEvery { getCoinInfoUseCase(fromSymbol) } returns flowOf(ResultStatus.Success(mockCoin))

            viewModel = createViewModel()
            viewModel.getCoinInfo(fromSymbol)

            val screenState = viewModel.screenState.observeForTesting()

            Assert.assertTrue(screenState.values[0] is CoinInfoScreenState.Content)
            Assert.assertEquals(
                mockCoin,
                (screenState.values[0] as CoinInfoScreenState.Content).coin,
            )
        }

    @Test
    fun `getCoinInfo should emit Loading then Error when failure`() =
        runTest {
            val fromSymbol = "BTC"
            val error = ErrorType.NETWORK_ERROR
            val errorMessage = "Network error"

            coEvery { getCoinInfoUseCase(fromSymbol) } returns flowOf(ResultStatus.Error(error))
            coEvery { errorMessageProvider.getErrorMessage(error) } returns errorMessage

            viewModel = createViewModel()
            viewModel.getCoinInfo(fromSymbol)

            val screenState = viewModel.screenState.observeForTesting()

            Assert.assertTrue(screenState.values[0] is CoinInfoScreenState.Error)
            Assert.assertEquals(
                errorMessage,
                (screenState.values[0] as CoinInfoScreenState.Error).message,
            )
        }

    @Test
    fun `should handle exception in coroutineExceptionHandler`() =
        runTest {
            val fromSymbol = "BTC"
            val exception = IOException("Network error")
            val errorType = ErrorType.NETWORK_ERROR
            val errorMessage = "Network error"

            coEvery { getCoinInfoUseCase(fromSymbol) } throws exception
            coEvery { errorMessageProvider.getErrorMessage(errorType) } returns errorMessage

            viewModel = createViewModel()
            viewModel.getCoinInfo(fromSymbol)

            val screenState = viewModel.screenState.observeForTesting()

            Assert.assertTrue(screenState.values[0] is CoinInfoScreenState.Error)
            Assert.assertEquals(
                errorMessage,
                (screenState.values[0] as CoinInfoScreenState.Error).message,
            )
        }

    private fun createViewModel(): CoinInfoViewModel =
        CoinInfoViewModel(
            getCoinInfoUseCase,
            errorMessageProvider,
        )
}