package com.grebnev.cryptoprice.domain.repository

import com.grebnev.core.wrappers.ErrorType
import com.grebnev.core.wrappers.ResultStatus
import com.grebnev.cryptoprice.domain.entity.News
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getNewsForCoin(category: String): Flow<ResultStatus<List<News>, ErrorType>>
}