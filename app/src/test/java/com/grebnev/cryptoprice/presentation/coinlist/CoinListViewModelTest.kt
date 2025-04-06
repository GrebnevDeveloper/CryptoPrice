package com.grebnev.cryptoprice.presentation.coinlist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.grebnev.core.wrappers.ErrorType
import com.grebnev.core.wrappers.ResultStatus
import com.grebnev.cryptoprice.domain.entity.Coin
import com.grebnev.cryptoprice.domain.usecase.GetCoinListUseCase
import com.grebnev.cryptoprice.domain.usecase.GetTimeLastUpdate
import com.grebnev.cryptoprice.domain.usecase.LoadDataUseCase
import com.grebnev.cryptoprice.presentation.base.error.ErrorMessageProvider
import com.grebnev.cryptoprice.presentation.coinlist.CoinListScreenState
import com.grebnev.cryptoprice.presentation.coinlist.CoinListViewModel
import com.grebnev.cryptoprice.testutils.observeForTesting
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
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

@OptIn(ExperimentalCoroutinesApi::class)
class CoinListViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var loadDataUseCase: LoadDataUseCase

    @MockK
    private lateinit var getCoinListUseCase: GetCoinListUseCase

    @MockK
    private lateinit var getTimeLastUpdate: GetTimeLastUpdate

    @MockK
    private lateinit var errorMessageProvider: ErrorMessageProvider

    private lateinit var viewModel: CoinListViewModel
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
    fun `init should call loadDataUseCase and getLastUpdate`() =
        runTest {
            coEvery { getCoinListUseCase() } returns flowOf(ResultStatus.Success(emptyList()))
            coEvery { getTimeLastUpdate() } returns flowOf("12:00:00")

            viewModel = createViewModel()

            coVerify { loadDataUseCase() }
            coVerify { getTimeLastUpdate() }
        }

    @Test
    fun `screenState should emit Loading first then Content when success`() =
        runTest {
            val coins = listOf(mockk<Coin>())
            coEvery { getCoinListUseCase() } returns flowOf(ResultStatus.Success(coins))
            coEvery { getTimeLastUpdate() } returns flowOf("12:00:00")

            viewModel = createViewModel()

            val screenState = viewModel.screenState.observeForTesting()

            Assert.assertTrue(screenState.values[0] is CoinListScreenState.Loading)
            val content = screenState.values[1] as CoinListScreenState.Content
            Assert.assertEquals(coins, content.coins)
            Assert.assertEquals("12:00:00", content.timeLastUpdate)
        }

    @Test
    fun `screenState should emit Error when repository returns error`() =
        runTest {
            val error = ErrorType.NETWORK_ERROR
            val errorMessage = "Network error"
            coEvery { getCoinListUseCase() } returns flowOf(ResultStatus.Error(error))
            coEvery { errorMessageProvider.getErrorMessage(error) } returns errorMessage

            viewModel = createViewModel()

            val screenState = viewModel.screenState.observeForTesting()

            Assert.assertTrue(screenState.values[0] is CoinListScreenState.Loading)
            Assert.assertEquals(
                errorMessage,
                (screenState.values[1] as CoinListScreenState.Error).message,
            )
        }

    @Test
    fun `screenState should handle flow with multiple emissions`() =
        runTest {
            val coins1 = listOf(mockk<Coin>())
            val coins2 = listOf(mockk<Coin>())
            val error = ErrorType.NETWORK_ERROR
            val errorMessage = "Network error"

            coEvery { getCoinListUseCase() } returns
                flow {
                    emit(ResultStatus.Success(coins1))
                    emit(ResultStatus.Error(error))
                    emit(ResultStatus.Success(coins2))
                }
            coEvery { getTimeLastUpdate() } returns flowOf("12:00:00")
            coEvery { errorMessageProvider.getErrorMessage(error) } returns errorMessage

            viewModel = createViewModel()

            val screenState = viewModel.screenState.observeForTesting()

            Assert.assertTrue(screenState.values[0] is CoinListScreenState.Loading)
            Assert.assertEquals(
                coins1,
                (screenState.values[1] as CoinListScreenState.Content).coins,
            )
            Assert.assertEquals(
                errorMessage,
                (screenState.values[2] as CoinListScreenState.Error).message,
            )
            Assert.assertEquals(
                coins2,
                (screenState.values[3] as CoinListScreenState.Content).coins,
            )
        }

    private fun createViewModel(): CoinListViewModel =
        CoinListViewModel(
            loadDataUseCase,
            getCoinListUseCase,
            getTimeLastUpdate,
            errorMessageProvider,
        )
}