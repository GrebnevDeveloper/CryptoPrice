package com.grebnev.cryptoprice.workers

import android.content.Context
import android.os.Build
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.grebnev.cryptoprice.data.database.CoinDao
import com.grebnev.cryptoprice.data.database.CoinDbModel
import com.grebnev.cryptoprice.data.mapper.CoinMapper
import com.grebnev.cryptoprice.data.network.ApiService
import com.grebnev.cryptoprice.data.network.model.coin.CoinDto
import com.grebnev.cryptoprice.data.network.model.coin.CoinJsonContainerDto
import com.grebnev.cryptoprice.data.network.model.coin.CoinNameListDto
import com.grebnev.cryptoprice.data.workers.RefreshDataWorker
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.IOException
import kotlin.time.Duration.Companion.milliseconds

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.Q])
class RefreshDataWorkerTest {
    @MockK
    private lateinit var mockCoinDao: CoinDao

    @MockK
    private lateinit var mockApiService: ApiService

    @MockK
    private lateinit var mockMapper: CoinMapper

    private lateinit var worker: RefreshDataWorker

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        worker =
            TestListenableWorkerBuilder<RefreshDataWorker>(
                context = RuntimeEnvironment.getApplication(),
            ).setWorkerFactory(createTestWorkerFactory())
                .build()
    }

    private fun createTestWorkerFactory(): WorkerFactory =
        object : WorkerFactory() {
            override fun createWorker(
                appContext: Context,
                workerClassName: String,
                workerParameters: WorkerParameters,
            ): ListenableWorker? =
                when (workerClassName) {
                    RefreshDataWorker::class.qualifiedName ->
                        RefreshDataWorker(
                            appContext,
                            workerParameters,
                            mockCoinDao,
                            mockApiService,
                            mockMapper,
                        )

                    else -> null
                }
        }

    @Test
    fun `doWork returns success when API calls succeed`() =
        runTest {
            val mockTopCoins = mockk<CoinNameListDto>()
            val mockJsonContainer = mockk<CoinJsonContainerDto>()
            val mockCoinDto = mockk<CoinDto>()
            val mockDbModel = mockk<CoinDbModel>()

            coEvery { mockApiService.getTopCoinsInfo(any()) } returns mockTopCoins
            every { mockMapper.mapNamesListToString(mockTopCoins) } returns "BTC,ETH"
            coEvery { mockApiService.getFullPriceList(any()) } returns mockJsonContainer
            every { mockMapper.mapJsonContainerDtoToCoinDtoList(mockJsonContainer) } returns
                listOf(mockCoinDto)
            every { mockMapper.mapDtoToDbModel(mockCoinDto) } returns mockDbModel
            coEvery { mockCoinDao.insertCoinList(any()) } just Runs

            worker.doWork()

            coVerify { mockApiService.getTopCoinsInfo(any()) }
            verify { mockMapper.mapNamesListToString(mockTopCoins) }
            coVerify { mockApiService.getFullPriceList(any()) }
            verify { mockMapper.mapJsonContainerDtoToCoinDtoList(mockJsonContainer) }
            verify { mockMapper.mapDtoToDbModel(mockCoinDto) }
            coVerify { mockCoinDao.insertCoinList(listOf(mockDbModel)) }
        }

    @Test
    fun `doWork returns failure when API call fails`() =
        runTest {
            coEvery { mockApiService.getTopCoinsInfo(any()) } throws IOException("Network error")

            val result = worker.doWork()

            assertEquals(ListenableWorker.Result.failure(), result)
        }

    @Test
    fun `makeRequest creates correct work request`() {
        val request = RefreshDataWorker.makeRequest()

        assertEquals(RefreshDataWorker::class.qualifiedName, request.workSpec.workerClassName)
        assertFalse(request.workSpec.isPeriodic)
    }

    @Test
    fun `makeRequestWithTimeout has correct delay`() {
        val request = RefreshDataWorker.makeRequestWithTimeout()

        assertEquals(
            RefreshDataWorker.REFRESH_TIMEOUT_SECONDS,
            request.workSpec.initialDelay.milliseconds.inWholeSeconds,
        )
    }
}