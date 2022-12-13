package com.grebnev.cryptoprice.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CoinDao {
    @Query("SELECT * FROM full_price_list ORDER BY lastUpdate DESC")
    fun getCoinList(): LiveData<List<CoinDbModel>>

    @Query("SELECT * FROM full_price_list WHERE fromSymbol == :fSym LIMIT 1")
    fun getCoinFromSymbol(fSym: String): LiveData<CoinDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCoinList(coinDDbModelList: List<CoinDbModel>)
}