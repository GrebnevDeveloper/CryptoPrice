package com.grebnev.cryptoprice.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.grebnev.cryptoprice.data.database.CoinDao
import com.grebnev.cryptoprice.data.mapper.CoinMapper
import com.grebnev.cryptoprice.data.workers.RefreshDataWorker
import com.grebnev.cryptoprice.domain.CoinRepository
import com.grebnev.cryptoprice.domain.entity.Coin
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    private val application: Application,
    private val coinDao: CoinDao,
    private val mapper: CoinMapper
) : CoinRepository {

    override fun getCoinList(): LiveData<List<Coin>> = Transformations.map(
        coinDao.getCoinList()
    ) {
        it.map {
            mapper.mapDbModelToEntity(it)
        }
    }

    override fun getCoinItem(fromSymbol: String): LiveData<Coin> = Transformations.map(
        coinDao.getCoinFromSymbol(fromSymbol)
    ) {
        mapper.mapDbModelToEntity(it)
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