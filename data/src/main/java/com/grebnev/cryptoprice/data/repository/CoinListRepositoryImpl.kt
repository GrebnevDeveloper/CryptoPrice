package com.grebnev.cryptoprice.data.repository

import android.app.Application
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.grebnev.cryptoprice.data.database.CoinDao
import com.grebnev.cryptoprice.data.mapper.CoinMapper
import com.grebnev.cryptoprice.data.workers.RefreshDataWorker
import com.grebnev.cryptoprice.domain.entity.Coin
import com.grebnev.cryptoprice.domain.repository.CoinListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CoinListRepositoryImpl
    @Inject
    constructor(
        private val application: Application,
        private val coinDao: CoinDao,
        private val mapper: CoinMapper,
    ) : CoinListRepository {
        override fun getCoinList(): Flow<List<Coin>> =
            coinDao.getCoinList().map { coinList ->
                coinList.map { coinDbModel ->
                    mapper.mapDbModelToEntity(coinDbModel)
                }
            }

        override fun loadData() {
            val workManager = WorkManager.getInstance(application)
            workManager.enqueueUniqueWork(
                RefreshDataWorker.NAME,
                ExistingWorkPolicy.REPLACE,
                RefreshDataWorker.makeRequest(),
            )
        }

        override fun getTimeLastUpdate(): Flow<String> =
            coinDao.getTimeLastUpdate().map { timeLastUpdate ->
                mapper.mapTimeLastUpdateDbModelToEntity(timeLastUpdate)
            }
    }