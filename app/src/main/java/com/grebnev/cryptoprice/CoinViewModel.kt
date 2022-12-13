package com.grebnev.cryptoprice

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.grebnev.cryptoprice.data.api.ApiFactory
import com.grebnev.cryptoprice.data.database.AppDatabase
import com.grebnev.cryptoprice.data.api.model.CoinDto
import com.grebnev.cryptoprice.data.api.model.CoinJsonContainerDto
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class CoinViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val compositeDisposable = CompositeDisposable()

    val priceList = db.coinDao().getCoinList()

    init {
        loadData()
    }

    fun getDetailInfo(fSym: String): LiveData<CoinDto> {
        return db.coinDao().getCoinFromSymbol(fSym)
    }

    private fun loadData() {
        val disposable = ApiFactory.apiService.getTopCoinsInfo(limit = 50)
            .map { it.names?.map { it.coinNameDto?.name }?.joinToString(",").toString() }
            .flatMap { ApiFactory.apiService.getFullPriceList(fSyms = it) }
            .map { getPriceListFromRawData(it) }
            .delaySubscription(10, TimeUnit.SECONDS)
            .repeat()
            .retry()
            .subscribeOn(Schedulers.io())
            .subscribe({
                db.coinDao().insertCoinList(it)
                Log.d("TEST_OF_LOADING_DATA", "Success: $it")
            }, {
                it.message?.let { message -> Log.d("TEST_OF_LOADING_DATA", "Failure: $message") }
            })
        compositeDisposable.add(disposable)
    }

    private fun getPriceListFromRawData(
        coinJsonContainerDto: CoinJsonContainerDto
    ): List<CoinDto> {
        val result = ArrayList<CoinDto>()
        val jsonObject = coinJsonContainerDto.json ?: return result
        val coinKeySet = jsonObject.keySet()
        for (coinKey in coinKeySet) {
            val currencyJson = jsonObject.getAsJsonObject(coinKey)
            val currencyKeySet = currencyJson.keySet()
            for (currencyKey in currencyKeySet) {
                val priceInfo = Gson().fromJson(
                    currencyJson.getAsJsonObject(currencyKey),
                    CoinDto::class.java
                )
                result.add(priceInfo)
            }
        }
        return result
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}