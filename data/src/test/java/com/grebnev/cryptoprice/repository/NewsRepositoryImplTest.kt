package com.grebnev.cryptoprice.repository

import app.cash.turbine.test
import com.grebnev.core.wrappers.ErrorType
import com.grebnev.core.wrappers.ResultStatus
import com.grebnev.cryptoprice.data.mapper.NewsMapper
import com.grebnev.cryptoprice.data.network.ApiService
import com.grebnev.cryptoprice.data.network.model.news.NewsResponseDto
import com.grebnev.cryptoprice.data.repository.NewsRepositoryImpl
import com.grebnev.cryptoprice.domain.entity.News
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
class NewsRepositoryImplTest {
    @MockK
    private lateinit var apiService: ApiService

    @MockK
    private lateinit var mapper: NewsMapper
    private lateinit var repository: NewsRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = NewsRepositoryImpl(apiService, mapper)
    }

    @Test
    fun `getNewsForCoin should emit Success with news when API call succeeds`() =
        runTest {
            val category = "BTC"
            val newsResponseDto = mockk<NewsResponseDto>()
            val expectedNews = listOf(mockk<News>())

            coEvery { apiService.getNewsForCoin(category = category) } returns newsResponseDto
            every { mapper.mapNewsResponseToNewsEntity(newsResponseDto) } returns expectedNews

            repository.getNewsForCoin(category).test {
                val result = awaitItem()

                assertEquals(ResultStatus.Success(expectedNews), result)

                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()

            coVerify { apiService.getNewsForCoin(category = category) }
            verify { mapper.mapNewsResponseToNewsEntity(newsResponseDto) }
        }

    @Test
    fun `getNewsForCoin should emit Error when API call fails`() =
        runTest {
            val category = "BTC"
            val expectedError = ErrorType.NETWORK_ERROR

            coEvery { apiService.getNewsForCoin(category = category) } throws IOException()

            repository.getNewsForCoin(category).test(timeout = 13.seconds) {
                val result = awaitItem()

                assertTrue(result is ResultStatus.Error)
                assertEquals(expectedError, (result as ResultStatus.Error).error)

                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()

            coVerify(exactly = 4) {
                apiService.getNewsForCoin(category = category)
            }
        }

    @Test
    fun `getNewsForCoin should retry when API call fails and then succeed`() =
        runTest {
            val category = "BTC"
            val newsResponseDto = mockk<NewsResponseDto>()
            val expectedNews = listOf(mockk<News>())

            coEvery { apiService.getNewsForCoin(category = category) }
                .throws(IOException())
                .andThenThrows(IOException())
                .andThen(newsResponseDto)

            every { mapper.mapNewsResponseToNewsEntity(newsResponseDto) } returns expectedNews

            repository.getNewsForCoin(category).test(timeout = 13.seconds) {
                val result = awaitItem()

                assertEquals(ResultStatus.Success(expectedNews), result)

                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()

            coVerify(exactly = 3) {
                apiService.getNewsForCoin(category = category)
            }
        }

    @Test
    fun `getNewsForCoin should handle empty news list correctly`() =
        runTest {
            val category = "BTC"
            val newsResponseDto = mockk<NewsResponseDto>()

            coEvery { apiService.getNewsForCoin(category = category) } returns newsResponseDto
            every { mapper.mapNewsResponseToNewsEntity(newsResponseDto) } returns emptyList()

            repository.getNewsForCoin(category).test {
                val result = awaitItem()

                assertTrue(result is ResultStatus.Success)
                assertTrue((result as ResultStatus.Success).data.isEmpty())

                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()
        }
}