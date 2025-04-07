package com.grebnev.cryptoprice.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDao {
    @Query("SELECT * FROM full_price_list ORDER BY mktCap DESC")
    fun getCoinList(): Flow<List<CoinDbModel>>

    @Query("SELECT * FROM full_price_list WHERE fromSymbol == :fSym LIMIT 1")
    fun getCoinFromSymbol(fSym: String): Flow<CoinDbModel>

    @Query("SELECT lastUpdate FROM full_price_list ORDER BY lastUpdate DESC LIMIT 1")
    fun getTimeLastUpdate(): Flow<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinList(coinDDbModelList: List<CoinDbModel>)
}