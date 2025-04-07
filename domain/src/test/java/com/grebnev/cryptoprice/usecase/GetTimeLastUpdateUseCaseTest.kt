package com.grebnev.cryptoprice.usecase

import com.grebnev.cryptoprice.domain.repository.CoinListRepository
import com.grebnev.cryptoprice.domain.usecase.GetTimeLastUpdateUseCase
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetTimeLastUpdateUseCaseTest {
    @MockK
    private lateinit var repository: CoinListRepository

    private lateinit var useCase: GetTimeLastUpdateUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = GetTimeLastUpdateUseCase(repository)
    }

    @Test
    fun `invoke should return time from repository`() {
        val mockFlow = flowOf("12:00:00")
        every { repository.getTimeLastUpdate() } returns mockFlow

        val result = useCase()

        assertEquals(mockFlow, result)
        verify { repository.getTimeLastUpdate() }
    }
}