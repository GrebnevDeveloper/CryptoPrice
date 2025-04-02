package com.grebnev.cryptoprice.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.grebnev.cryptoprice.data.database.CoinDao
import com.grebnev.cryptoprice.data.mapper.CoinMapper
import com.grebnev.cryptoprice.data.network.ApiService
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RefreshDataWorker(
    private val context: Context,
    workerParameters: WorkerParameters,
    private val coinDao: CoinDao,
    private val apiService: ApiService,
    private val mapper: CoinMapper,
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        try {
            loadCoinList()

            val workManager = WorkManager.getInstance(context)

            workManager.enqueueUniqueWork(
                REFRESH_WORKER_NAME,
                ExistingWorkPolicy.REPLACE,
                makeRequestWithTimeout(),
            )

            return Result.success()
        } catch (exception: Exception) {
            Timber.e(exception)
            return Result.failure()
        }
    }

    private suspend fun loadCoinList() {
        val topCoins = apiService.getTopCoinsInfo(limit = TOP_COINS_LIMIT)
        val fSyms = mapper.mapNamesListToString(topCoins)
        val jsonContainer = apiService.getFullPriceList(fSyms = fSyms)
        val coinDtoList = mapper.mapJsonContainerDtoToCoinDtoList(jsonContainer)
        val dbModelList =
            coinDtoList.map {
                mapper.mapDtoToDbModel(it)
            }
        coinDao.insertCoinList(dbModelList)
    }

    companion object {
        private const val TOP_COINS_LIMIT = 50
        const val REFRESH_WORKER_NAME = "refresh_data_worker"
        const val REFRESH_TIMEOUT_SECONDS = 10L
        const val REFRESH_TIMEOUT_AFTER_ERROR = 5000L

        fun makeRequest(): OneTimeWorkRequest = OneTimeWorkRequestBuilder<RefreshDataWorker>().build()

        fun makeRequestWithTimeout(): OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<RefreshDataWorker>()
                .setInitialDelay(REFRESH_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build()
    }

    class Factory
        @Inject
        constructor(
            private val coinDao: CoinDao,
            private val apiService: ApiService,
            private val mapper: CoinMapper,
        ) : ChildCoinWorkerFactory {
            override fun create(
                context: Context,
                workerParameters: WorkerParameters,
            ): ListenableWorker = RefreshDataWorker(context, workerParameters, coinDao, apiService, mapper)
        }
}