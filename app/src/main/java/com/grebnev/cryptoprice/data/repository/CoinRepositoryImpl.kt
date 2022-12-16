package com.grebnev.cryptoprice.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.grebnev.cryptoprice.data.database.AppDatabase
import com.grebnev.cryptoprice.data.mapper.CoinMapper
import com.grebnev.cryptoprice.data.workers.RefreshDataWorker
import com.grebnev.cryptoprice.domain.CoinRepository
import com.grebnev.cryptoprice.domain.entity.Coin

class CoinRepositoryImpl(private val context: Context) : CoinRepository {

    private val coinDao = AppDatabase.getInstance(context).coinDao()

    private val mapper = CoinMapper()

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
        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniqueWork(
            RefreshDataWorker.NAME,
            ExistingWorkPolicy.REPLACE,
            RefreshDataWorker.makeRequest()
        )
    }
}