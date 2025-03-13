package com.grebnev.cryptoprice.data.repository

import com.grebnev.cryptoprice.data.database.CoinDao
import com.grebnev.cryptoprice.data.mapper.CoinMapper
import com.grebnev.cryptoprice.domain.entity.Coin
import com.grebnev.cryptoprice.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CoinRepositoryImpl
    @Inject
    constructor(
        private val coinDao: CoinDao,
        private val mapper: CoinMapper,
    ) : CoinRepository {
        override fun getCoinItem(fromSymbol: String): Flow<Coin> =
            coinDao.getCoinFromSymbol(fromSymbol).map { coinDbModel ->
                mapper.mapDbModelToEntity(coinDbModel)
            }
    }