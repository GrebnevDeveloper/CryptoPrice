package com.grebnev.cryptoprice.repository

import app.cash.turbine.test
import com.grebnev.core.wrappers.ErrorType
import com.grebnev.core.wrappers.ResultStatus
import com.grebnev.cryptoprice.data.database.CoinDao
import com.grebnev.cryptoprice.data.database.CoinDbModel
import com.grebnev.cryptoprice.data.mapper.CoinMapper
import com.grebnev.cryptoprice.data.repository.CoinRepositoryImpl
import com.grebnev.cryptoprice.domain.entity.Coin
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okio.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.sql.SQLException
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class CoinRepositoryImplTest {
    @MockK
    private lateinit var coinDao: CoinDao

    @MockK
    private lateinit var mapper: CoinMapper
    private lateinit var repository: CoinRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = CoinRepositoryImpl(coinDao, mapper)
    }

    @Test
    fun `getCoinInfo should emit Success with coin when database returns data`() =
        runTest {
            val fromSymbol = "BTC"
            val coinDbModel = mockk<CoinDbModel>()
            val expectedCoin = mockk<Coin>()

            every { coinDao.getCoinFromSymbol(fromSymbol) } returns flowOf(coinDbModel)
            every { mapper.mapDbModelToEntity(coinDbModel) } returns expectedCoin

            repository.getCoinInfo(fromSymbol).test {
                val result = awaitItem()
                assertEquals(ResultStatus.Success(expectedCoin), result)
                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()

            verify { coinDao.getCoinFromSymbol(fromSymbol) }
            verify { mapper.mapDbModelToEntity(coinDbModel) }
        }

    @Test
    fun `getCoinInfo should emit Error when database throws exception`() =
        runTest {
            val fromSymbol = "BTC"
            val expectedError = ErrorType.DATABASE_ERROR

            every { coinDao.getCoinFromSymbol(fromSymbol) } returns flow { throw SQLException() }

            repository.getCoinInfo(fromSymbol).test(timeout = 13.seconds) {
                val result = awaitItem()

                assertTrue(result is ResultStatus.Error)
                assertEquals(expectedError, (result as ResultStatus.Error).error)

                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()

            verify(exactly = 4) {
                coinDao.getCoinFromSymbol(fromSymbol)
            }
        }

    @Test
    fun `getCoinInfo should retry when database fails and then succeeds`() =
        runTest {
            val fromSymbol = "BTC"
            val coinDbModel = mockk<CoinDbModel>()
            val expectedCoin = mockk<Coin>()

            coEvery { coinDao.getCoinFromSymbol(fromSymbol) }
                .returns(flow { throw IOException() })
                .andThen(flow { throw IOException() })
                .andThen(flowOf(coinDbModel))

            every { mapper.mapDbModelToEntity(coinDbModel) } returns expectedCoin

            repository.getCoinInfo(fromSymbol).test(timeout = 13.seconds) {
                val result = awaitItem()

                assertEquals(ResultStatus.Success(expectedCoin), result)

                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()

            verify(exactly = 3) {
                coinDao.getCoinFromSymbol(fromSymbol)
            }
        }

    @Test
    fun `getCoinInfo should use correct dispatcher`() =
        runTest {
            val fromSymbol = "BTC"
            val coinDbModel = mockk<CoinDbModel>()
            val expectedCoin = mockk<Coin>()

            every { coinDao.getCoinFromSymbol(fromSymbol) } returns flowOf(coinDbModel)
            every { mapper.mapDbModelToEntity(coinDbModel) } returns expectedCoin

            repository.getCoinInfo(fromSymbol).test {
                awaitItem()
                awaitComplete()
            }
            advanceUntilIdle()

            verify { coinDao.getCoinFromSymbol(fromSymbol) }
            verify { mapper.mapDbModelToEntity(coinDbModel) }
        }
}