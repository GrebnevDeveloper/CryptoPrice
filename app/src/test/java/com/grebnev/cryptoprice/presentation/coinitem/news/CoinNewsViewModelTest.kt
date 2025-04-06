package com.grebnev.cryptoprice.presentation.coinitem.news

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.grebnev.core.wrappers.ErrorType
import com.grebnev.core.wrappers.ResultStatus
import com.grebnev.cryptoprice.domain.entity.News
import com.grebnev.cryptoprice.domain.usecase.GetNewsForCoinUseCase
import com.grebnev.cryptoprice.presentation.base.error.ErrorMessageProvider
import com.grebnev.cryptoprice.presentation.coinitem.news.CoinNewsScreenState
import com.grebnev.cryptoprice.presentation.coinitem.news.CoinNewsViewModel
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
class CoinNewsViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var getNewsForCoinUseCase: GetNewsForCoinUseCase

    @MockK
    private lateinit var errorMessageProvider: ErrorMessageProvider

    private lateinit var viewModel: CoinNewsViewModel
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

            Assert.assertTrue(screenState.values[0] is CoinNewsScreenState.Initial)
        }

    @Test
    fun `getNewsForCoin should emit Loading then Content when success`() =
        runTest {
            val mockNews =
                listOf(
                    mockk<News>(),
                    mockk<News>(),
                )
            val category = "BTC"

            coEvery { getNewsForCoinUseCase(category) } returns flowOf(ResultStatus.Success(mockNews))

            viewModel = createViewModel()
            viewModel.getNewsForCoin(category)

            val screenState = viewModel.screenState.observeForTesting()

            Assert.assertTrue(screenState.values[0] is CoinNewsScreenState.Content)
            Assert.assertEquals(
                mockNews,
                (screenState.values[0] as CoinNewsScreenState.Content).news,
            )
        }

    @Test
    fun `getNewsForCoin should emit Loading then Error when failure`() =
        runTest {
            val category = "BTC"
            val error = ErrorType.NETWORK_ERROR
            val errorMessage = "Network error"

            coEvery { getNewsForCoinUseCase(category) } returns flowOf(ResultStatus.Error(error))
            coEvery { errorMessageProvider.getErrorMessage(error) } returns errorMessage

            viewModel = createViewModel()
            viewModel.getNewsForCoin(category)

            val screenState = viewModel.screenState.observeForTesting()

            Assert.assertTrue(screenState.values[0] is CoinNewsScreenState.Error)
            Assert.assertEquals(
                errorMessage,
                (screenState.values[0] as CoinNewsScreenState.Error).message,
            )
        }

    @Test
    fun `should handle exception in coroutineExceptionHandler`() =
        runTest {
            val category = "BTC"
            val exception = IOException("Network error")
            val errorType = ErrorType.NETWORK_ERROR
            val errorMessage = "Network error"

            coEvery { getNewsForCoinUseCase(category) } throws exception
            coEvery { errorMessageProvider.getErrorMessage(errorType) } returns errorMessage

            viewModel = createViewModel()
            viewModel.getNewsForCoin(category)

            val screenState = viewModel.screenState.observeForTesting()

            Assert.assertTrue(screenState.values[0] is CoinNewsScreenState.Error)
            Assert.assertEquals(
                errorMessage,
                (screenState.values[0] as CoinNewsScreenState.Error).message,
            )
        }

    @Test
    fun `should handle empty news list`() =
        runTest {
            val category = "BTC"
            val emptyNews = emptyList<News>()

            coEvery { getNewsForCoinUseCase(category) } returns
                flowOf(
                    ResultStatus.Success(
                        emptyNews,
                    ),
                )

            viewModel = createViewModel()
            viewModel.getNewsForCoin(category)

            val screenState = viewModel.screenState.observeForTesting()

            Assert.assertTrue(screenState.values[0] is CoinNewsScreenState.Content)
            Assert.assertTrue((screenState.values[0] as CoinNewsScreenState.Content).news.isEmpty())
        }

    private fun createViewModel(): CoinNewsViewModel =
        CoinNewsViewModel(
            getNewsForCoinUseCase,
            errorMessageProvider,
        )
}