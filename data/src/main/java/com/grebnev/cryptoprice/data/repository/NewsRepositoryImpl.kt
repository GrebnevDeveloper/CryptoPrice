package com.grebnev.cryptoprice.data.repository

import com.grebnev.core.handlers.ErrorHandler
import com.grebnev.core.wrappers.ErrorType
import com.grebnev.core.wrappers.ResultStatus
import com.grebnev.cryptoprice.data.mapper.NewsMapper
import com.grebnev.cryptoprice.data.network.ApiService
import com.grebnev.cryptoprice.domain.entity.News
import com.grebnev.cryptoprice.domain.repository.NewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

class NewsRepositoryImpl
    @Inject
    constructor(
        private val apiService: ApiService,
        private val mapper: NewsMapper,
    ) : NewsRepository {
        override fun getNewsForCoin(category: String): Flow<ResultStatus<List<News>, ErrorType>> =
            flow {
                val response = apiService.getNewsForCoin(category = category)
                val news = mapper.mapNewsResponseToNewsEntity(response)
                emit(ResultStatus.Success(news) as ResultStatus<List<News>, ErrorType>)
            }.catch { throwable ->
                Timber.e(throwable)
                val errorType = ErrorHandler.getErrorTypeByError(throwable)
                emit(ResultStatus.Error(errorType))
            }.flowOn(Dispatchers.Default)
    }