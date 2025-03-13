package com.grebnev.cryptoprice.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.grebnev.cryptoprice.data.database.CoinDao
import com.grebnev.cryptoprice.data.mapper.CoinMapper
import com.grebnev.cryptoprice.data.network.ApiService
import kotlinx.coroutines.delay
import javax.inject.Inject

class RefreshDataWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val coinDao: CoinDao,
    private val apiService: ApiService,
    private val mapper: CoinMapper,
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        while (true) {
            try {
                val topCoins = apiService.getTopCoinsInfo(limit = 50)
                val fSyms = mapper.mapNamesListToString(topCoins)
                val jsonContainer = apiService.getFullPriceList(fSyms = fSyms)
                val coinDtoList = mapper.mapJsonContainerDtoToCoinDtoList(jsonContainer)
                val dbModelList =
                    coinDtoList.map {
                        mapper.mapDtoToDbModel(it)
                    }
                coinDao.insertCoinList(dbModelList)
            } catch (e: Exception) {
            }
            delay(10000)
        }
    }

    companion object {
        const val NAME = "refresh_data_worker"

        fun makeRequest(): OneTimeWorkRequest = OneTimeWorkRequestBuilder<RefreshDataWorker>().build()
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