package com.grebnev.cryptoprice.data.repository

import android.app.Application
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.grebnev.core.ErrorHandler
import com.grebnev.core.ErrorType
import com.grebnev.core.ResultState
import com.grebnev.core.mergeWith
import com.grebnev.cryptoprice.data.database.CoinDao
import com.grebnev.cryptoprice.data.mapper.CoinMapper
import com.grebnev.cryptoprice.data.workers.RefreshDataWorker
import com.grebnev.cryptoprice.domain.entity.Coin
import com.grebnev.cryptoprice.domain.repository.CoinListRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class CoinListRepositoryImpl
    @Inject
    constructor(
        private val application: Application,
        private val coinDao: CoinDao,
        private val mapper: CoinMapper,
    ) : CoinListRepository {
        private val coinListFlow: Flow<ResultState<List<Coin>, ErrorType>> =
            flow {
                coinDao
                    .getCoinList()
                    .map { coinList ->
                        coinList.map { coinDbModel ->
                            mapper.mapDbModelToEntity(coinDbModel)
                        }
                    }.collect {
                        emit(ResultState.Success(it) as ResultState<List<Coin>, ErrorType>)
                    }
            }.catch { throwable ->
                Timber.e(throwable)
                emit(ResultState.Error(ErrorHandler.getErrorTypeByError(throwable)))
            }

        private val refreshedListFlow = MutableSharedFlow<ResultState<List<Coin>, ErrorType>>()

        override val getCoinList: Flow<ResultState<List<Coin>, ErrorType>> =
            coinListFlow
                .mergeWith(refreshedListFlow)

        override suspend fun loadData() {
            val workManager = WorkManager.getInstance(application)
            workManager.enqueueUniqueWork(
                RefreshDataWorker.REFRESH_WORKER_NAME,
                ExistingWorkPolicy.REPLACE,
                RefreshDataWorker.makeRequest(),
            )
            trackingError(workManager)
        }

        private suspend fun trackingError(workManager: WorkManager) {
            workManager
                .getWorkInfosForUniqueWorkFlow(RefreshDataWorker.REFRESH_WORKER_NAME)
                .collect { workInfos ->
                    workInfos.forEach { workInfo ->
                        if (workInfo.state == WorkInfo.State.FAILED) {
                            Timber.e("Error in worker")
                            val outputError = workInfo.outputData.getString(RefreshDataWorker.ERROR_KEY)
                            val typeError = ErrorHandler.getErrorTypeByValue(outputError)
                            refreshedListFlow.emit(ResultState.Error(typeError))
                            delay(RefreshDataWorker.REFRESH_TIMEOUT_AFTER_ERROR)
                            loadData()
                        }
                    }
                }
        }

        override fun getTimeLastUpdate(): Flow<String> =
            coinDao.getTimeLastUpdate().map { timeLastUpdate ->
                mapper.mapTimeLastUpdateDbModelToEntity(timeLastUpdate)
            }
    }