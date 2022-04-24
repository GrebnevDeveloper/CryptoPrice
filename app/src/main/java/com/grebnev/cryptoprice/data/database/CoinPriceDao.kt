package com.grebnev.cryptoprice.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.grebnev.cryptoprice.data.pojo.price.CoinPrice

@Dao
interface CoinPriceDao {
    @Query("SELECT * FROM full_price_list ORDER BY lastUpdate DESC")
    fun getCoinPriceList(): LiveData<List<CoinPrice>>

    @Query("SELECT * FROM full_price_list WHERE fromSymbol == :fSym LIMIT 1")
    fun getCoinPriceFromSymbol(fSym: String): LiveData<CoinPrice>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCoinPriceList(coinPriceList: List<CoinPrice>)
}