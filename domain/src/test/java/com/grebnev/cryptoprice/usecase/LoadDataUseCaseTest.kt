package com.grebnev.cryptoprice.usecase

import com.grebnev.cryptoprice.domain.repository.CoinListRepository
import com.grebnev.cryptoprice.domain.usecase.LoadDataUseCase
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class LoadDataUseCaseTest {
    @MockK
    private lateinit var repository: CoinListRepository

    private lateinit var useCase: LoadDataUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = LoadDataUseCase(repository)
    }

    @Test
    fun `invoke should call repository loadData`() =
        runTest {
            coEvery { repository.loadData() } just Runs

            useCase()

            coVerify { repository.loadData() }
        }
}