package com.grebnev.cryptoprice.data.repository

import com.grebnev.core.handlers.ErrorHandler
import com.grebnev.core.wrappers.ErrorType
import com.grebnev.core.wrappers.ResultStatus
import com.grebnev.cryptoprice.data.mapper.BarMapper
import com.grebnev.cryptoprice.data.network.ApiService
import com.grebnev.cryptoprice.domain.entity.Bar
import com.grebnev.cryptoprice.domain.repository.BarRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class BarRepositoryImpl
    @Inject
    constructor(
        private val apiService: ApiService,
        private val mapper: BarMapper,
    ) : BarRepository {
        override fun getBarsForCoin(
            fromSymbol: String,
            timeFrame: String,
        ): Flow<ResultStatus<List<Bar>, ErrorType>> =
            flow {
                val response =
                    apiService.getBarsForCoin(
                        timeFrame = timeFrame,
                        fSyms = fromSymbol,
                    )
                val bars = mapper.mapBarContainerDtoToBarEntity(response)
                emit(ResultStatus.Success(bars) as ResultStatus<List<Bar>, ErrorType>)
            }.catch { throwable ->
                Timber.e(throwable)
                val typeError = ErrorHandler.getErrorTypeByError(throwable)
                emit(ResultStatus.Error(typeError))
            }
    }