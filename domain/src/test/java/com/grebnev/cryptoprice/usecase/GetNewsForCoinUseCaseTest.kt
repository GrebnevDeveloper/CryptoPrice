package com.grebnev.cryptoprice.usecase

import com.grebnev.core.wrappers.ResultStatus
import com.grebnev.cryptoprice.domain.entity.News
import com.grebnev.cryptoprice.domain.repository.NewsRepository
import com.grebnev.cryptoprice.domain.usecase.GetNewsForCoinUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetNewsForCoinUseCaseTest {
    @MockK
    private lateinit var repository: NewsRepository

    private lateinit var useCase: GetNewsForCoinUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = GetNewsForCoinUseCase(repository)
    }

    @Test
    fun `invoke should return news from repository`() =
        runTest {
            val mockNews = listOf(mockk<News>())
            val category = "BTC"
            coEvery { repository.getNewsForCoin(category) } returns flowOf(ResultStatus.Success(mockNews))

            val result = useCase(category).first()

            assertTrue(result is ResultStatus.Success)
            assertEquals(mockNews, (result as ResultStatus.Success).data)
        }
}