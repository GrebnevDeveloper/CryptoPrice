package com.grebnev.cryptoprice.usecase

import com.grebnev.core.wrappers.ResultStatus
import com.grebnev.cryptoprice.domain.entity.Coin
import com.grebnev.cryptoprice.domain.repository.CoinRepository
import com.grebnev.cryptoprice.domain.usecase.GetCoinInfoUseCase
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

class GetCoinInfoUseCaseTest {
    @MockK
    private lateinit var repository: CoinRepository

    private lateinit var useCase: GetCoinInfoUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = GetCoinInfoUseCase(repository)
    }

    @Test
    fun `invoke should return coin info from repository`() =
        runTest {
            val mockCoin = mockk<Coin>()
            val symbol = "BTC"
            coEvery { repository.getCoinInfo(symbol) } returns flowOf(ResultStatus.Success(mockCoin))

            val result = useCase(symbol).first()

            assertTrue(result is ResultStatus.Success)
            assertEquals(mockCoin, (result as ResultStatus.Success).data)
        }
}