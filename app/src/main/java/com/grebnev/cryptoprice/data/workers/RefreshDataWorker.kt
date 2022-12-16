package com.grebnev.cryptoprice.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.grebnev.cryptoprice.data.api.ApiFactory
import com.grebnev.cryptoprice.data.database.AppDatabase
import com.grebnev.cryptoprice.data.mapper.CoinMapper
import kotlinx.coroutines.delay

class RefreshDataWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private val coinDao = AppDatabase.getInstance(context).coinDao()
    private val apiService = ApiFactory.apiService

    private val mapper = CoinMapper()

    override suspend fun doWork(): Result {
        while (true) {
            try {
                val topCoins = apiService.getTopCoinsInfo(limit = 50)
                val fSyms = mapper.mapNamesListToString(topCoins)
                val jsonContainer = apiService.getFullPriceList(fSyms = fSyms)
                val coinDtoList = mapper.mapJsonContainerDtoToCoinDtoList(jsonContainer)
                val dbModelList = coinDtoList.map {
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

        fun makeRequest(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<RefreshDataWorker>().build()
        }
    }
}