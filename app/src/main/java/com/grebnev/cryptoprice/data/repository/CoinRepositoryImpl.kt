package com.grebnev.cryptoprice.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.grebnev.cryptoprice.data.api.ApiFactory
import com.grebnev.cryptoprice.data.database.AppDatabase
import com.grebnev.cryptoprice.data.mapper.CoinMapper
import com.grebnev.cryptoprice.domain.CoinRepository
import com.grebnev.cryptoprice.domain.entity.Coin
import kotlinx.coroutines.delay

class CoinRepositoryImpl(private val application: Application) : CoinRepository {

    private val coinDao = AppDatabase.getInstance(application).coinDao()
    private val apiService = ApiFactory.apiService
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

    override suspend fun loadData() {
        while (true) {
            val topCoins = apiService.getTopCoinsInfo(limit = 50)
            val fSyms = mapper.mapNamesListToString(topCoins)
            val jsonContainer = apiService.getFullPriceList(fSyms = fSyms)
            val coinDtoList = mapper.mapJsonContainerDtoToCoinDtoList(jsonContainer)
            val dbModelList = coinDtoList.map {
                mapper.mapDtoToDbModel(it)
            }
            coinDao.insertCoinList(dbModelList)
            delay(10000)
        }
    }
}