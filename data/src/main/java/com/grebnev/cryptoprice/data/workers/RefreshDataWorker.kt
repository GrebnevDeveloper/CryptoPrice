package com.grebnev.cryptoprice.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.grebnev.core.ErrorHandler
import com.grebnev.cryptoprice.data.database.CoinDao
import com.grebnev.cryptoprice.data.mapper.CoinMapper
import com.grebnev.cryptoprice.data.network.ApiService
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
            val typeError = ErrorHandler.getErrorTypeByError(exception)
            val outputError = workDataOf(ERROR_KEY to typeError.type)
            return Result.failure(outputError)
        }
    }

    private suspend fun loadCoinList() {
        val topCoins = apiService.getTopCoinsInfo(limit = 50)
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
        const val REFRESH_WORKER_NAME = "refresh_data_worker"
        const val ERROR_KEY = "error_key"
        const val REFRESH_TIMEOUT = 60L
        const val REFRESH_TIMEOUT_AFTER_ERROR = 5000L

        fun makeRequest(): OneTimeWorkRequest = OneTimeWorkRequestBuilder<RefreshDataWorker>().build()

        fun makeRequestWithTimeout(): OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<RefreshDataWorker>()
                .setInitialDelay(REFRESH_TIMEOUT, TimeUnit.SECONDS)
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