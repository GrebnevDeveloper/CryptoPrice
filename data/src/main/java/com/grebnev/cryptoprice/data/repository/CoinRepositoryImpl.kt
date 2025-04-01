package com.grebnev.cryptoprice.data.repository

import com.grebnev.core.handlers.ErrorHandler
import com.grebnev.core.wrappers.ErrorType
import com.grebnev.core.wrappers.ResultStatus
import com.grebnev.cryptoprice.data.database.CoinDao
import com.grebnev.cryptoprice.data.mapper.CoinMapper
import com.grebnev.cryptoprice.domain.entity.Coin
import com.grebnev.cryptoprice.domain.repository.CoinRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CoinRepositoryImpl
    @Inject
    constructor(
        private val coinDao: CoinDao,
        private val mapper: CoinMapper,
    ) : CoinRepository {
        override fun getCoinInfo(fromSymbol: String): Flow<ResultStatus<Coin, ErrorType>> =
            flow {
                coinDao
                    .getCoinFromSymbol(fromSymbol)
                    .map { coinDbModel ->
                        mapper.mapDbModelToEntity(coinDbModel)
                    }.collect {
                        emit(ResultStatus.Success(it) as ResultStatus<Coin, ErrorType>)
                    }
            }.catch { throwable ->
                emit(ResultStatus.Error(ErrorHandler.getErrorTypeByError(throwable)))
            }.flowOn(Dispatchers.Default)
    }