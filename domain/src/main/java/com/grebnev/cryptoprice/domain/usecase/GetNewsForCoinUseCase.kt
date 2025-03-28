package com.grebnev.cryptoprice.domain.usecase

import com.grebnev.core.wrappers.ErrorType
import com.grebnev.core.wrappers.ResultStatus
import com.grebnev.cryptoprice.domain.entity.News
import com.grebnev.cryptoprice.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNewsForCoinUseCase
    @Inject
    constructor(
        private val repository: NewsRepository,
    ) {
        operator fun invoke(category: String): Flow<ResultStatus<List<News>, ErrorType>> =
            repository.getNewsForCoin(category)
    }