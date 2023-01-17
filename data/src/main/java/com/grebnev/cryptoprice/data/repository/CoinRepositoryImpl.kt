package com.grebnev.cryptoprice.data.repository

import android.app.Application
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.grebnev.cryptoprice.data.database.CoinDao
import com.grebnev.cryptoprice.data.mapper.CoinMapper
import com.grebnev.cryptoprice.data.workers.RefreshDataWorker
import com.grebnev.cryptoprice.domain.repository.CoinRepository
import com.grebnev.cryptoprice.domain.entity.Coin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    private val application: Application,
    private val coinDao: CoinDao,
    private val mapper: CoinMapper
) : CoinRepository {

    override fun getCoinList(): Flow<List<Coin>> {
        return coinDao.getCoinList().map { coinList ->
            coinList.map { coinDbModel ->
                mapper.mapDbModelToEntity(coinDbModel)
            }
        }
    }

    override fun getCoinItem(fromSymbol: String): Flow<Coin> {
        return coinDao.getCoinFromSymbol(fromSymbol).map { coinDbModel ->
            mapper.mapDbModelToEntity(coinDbModel)
        }
    }

    override fun loadData() {
        val workManager = WorkManager.getInstance(application)
        workManager.enqueueUniqueWork(
            RefreshDataWorker.NAME,
            ExistingWorkPolicy.REPLACE,
            RefreshDataWorker.makeRequest()
        )
    }
}