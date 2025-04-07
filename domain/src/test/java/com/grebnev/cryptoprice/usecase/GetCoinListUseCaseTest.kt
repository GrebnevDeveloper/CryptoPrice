package com.grebnev.cryptoprice.usecase

import com.grebnev.core.wrappers.ResultStatus
import com.grebnev.cryptoprice.domain.entity.Coin
import com.grebnev.cryptoprice.domain.repository.CoinListRepository
import com.grebnev.cryptoprice.domain.usecase.GetCoinListUseCase
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetCoinListUseCaseTest {
    @MockK
    private lateinit var repository: CoinListRepository

    private lateinit var useCase: GetCoinListUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = GetCoinListUseCase(repository)
    }

    @Test
    fun `invoke should return coin list from repository`() {
        val mockFlow = flowOf(ResultStatus.Success(emptyList<Coin>()))
        every { repository.getCoinList } returns mockFlow

        val result = useCase()

        assertEquals(mockFlow, result)
        verify { repository.getCoinList }
    }
}