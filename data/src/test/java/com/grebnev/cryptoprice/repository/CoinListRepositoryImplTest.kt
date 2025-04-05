package com.grebnev.cryptoprice.repository

import android.app.Application
import android.os.Build
import app.cash.turbine.test
import com.grebnev.core.extensions.convertTimestampToTimeByPattern
import com.grebnev.core.wrappers.ErrorType
import com.grebnev.core.wrappers.ResultStatus
import com.grebnev.cryptoprice.data.database.CoinDao
import com.grebnev.cryptoprice.data.database.CoinDbModel
import com.grebnev.cryptoprice.data.mapper.CoinMapper
import com.grebnev.cryptoprice.data.repository.CoinListRepositoryImpl
import com.grebnev.cryptoprice.domain.entity.Coin
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.sql.SQLException
import java.util.TimeZone
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.Q])
class CoinListRepositoryImplTest {
    @MockK
    private lateinit var coinDao: CoinDao

    @MockK
    private lateinit var mapper: CoinMapper

    private lateinit var context: Application
    private lateinit var repository: CoinListRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        context = RuntimeEnvironment.getApplication()
        repository = CoinListRepositoryImpl(context, coinDao, mapper)
    }

    @Test
    fun `getCoinList should emit Success with mapped coins`() =
        runTest {
            val dbModels = listOf(mockk<CoinDbModel>(), mockk<CoinDbModel>())
            val coins = listOf(mockk<Coin>(), mockk<Coin>())

            every { coinDao.getCoinList() } returns flowOf(dbModels)
            every { mapper.mapDbModelToEntity(any()) } returnsMany coins

            repository.getCoinList.test {
                val result = awaitItem()

                assertTrue(result is ResultStatus.Success)
                assertEquals(coins, (result as ResultStatus.Success).data)

                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()

            verify { coinDao.getCoinList() }
            verify(exactly = 2) { mapper.mapDbModelToEntity(any()) }
        }

    @Test
    fun `getCoinList should emit Error when database fails`() =
        runTest {
            every { coinDao.getCoinList() } returns flow { throw SQLException() }

            repository.getCoinList.test(timeout = 13.seconds) {
                val result = awaitItem()

                assertTrue(result is ResultStatus.Error)
                assertEquals(ErrorType.DATABASE_ERROR, (result as ResultStatus.Error).error)

                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()

            verify(exactly = 4) { coinDao.getCoinList() }
        }

    @Test
    fun `getTimeLastUpdate should return formatted time`() =
        runTest {
            val timestamp = 1672531200L
            val pattern = "HH:mm:ss"
            val timeZone = TimeZone.getTimeZone("UTC")
            val expectedFormattedTime = "04:00:00"

            mockkStatic("com.grebnev.core.extensions.LongExKt")
            every { coinDao.getTimeLastUpdate() } returns flowOf(timestamp)
            every {
                any<Long>().convertTimestampToTimeByPattern(
                    pattern = eq(pattern),
                    timeZone = timeZone,
                )
            } answers {
                val timestamp = firstArg<Long>()
                val pattern = secondArg<String>()
                val tz = thirdArg<TimeZone>()
                timestamp.convertTimestampToTimeByPattern(pattern, tz)
            }

            repository.getTimeLastUpdate().test {
                val result = awaitItem()

                assertEquals(expectedFormattedTime, result)

                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()
            unmockkStatic("com.grebnev.core.extensions.LongExKt")
        }
}