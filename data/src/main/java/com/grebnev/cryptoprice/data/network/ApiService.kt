package com.grebnev.cryptoprice.data.network

import com.grebnev.cryptoprice.data.network.model.bars.BarContainerDto
import com.grebnev.cryptoprice.data.network.model.coin.CoinJsonContainerDto
import com.grebnev.cryptoprice.data.network.model.coin.CoinNameListDto
import com.grebnev.cryptoprice.data.network.model.news.NewsResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("top/totalvolfull")
    suspend fun getTopCoinsInfo(
        @Query(QUERY_PARAM_LIMIT) limit: Int = 10,
        @Query(QUERY_PARAM_TO_SYMBOL) tSym: String = CURRENCY,
    ): CoinNameListDto

    @GET("pricemultifull")
    suspend fun getFullPriceList(
        @Query(QUERY_PARAM_FROM_SYMBOLS) fSyms: String?,
        @Query(QUERY_PARAM_TO_SYMBOLS) tSyms: String = CURRENCY,
    ): CoinJsonContainerDto

    @GET("v2/{$PATH_PARAM_TIME_FRAME}")
    suspend fun getBarsForCoin(
        @Path(PATH_PARAM_TIME_FRAME) timeFrame: String,
        @Query(QUERY_PARAM_FROM_SYMBOL) fSyms: String?,
        @Query(QUERY_PARAM_LIMIT) limit: Int = 2000,
        @Query(QUERY_PARAM_TO_SYMBOL) tSym: String = CURRENCY,
    ): BarContainerDto

    @GET("v2/news/")
    suspend fun getNewsForCoin(
        @Query(QUERY_PARAM_LANG) lang: String = "EN",
        @Query(QUERY_PARAM_CATEGORIES) category: String?,
    ): NewsResponseDto

    companion object {
        private const val QUERY_PARAM_LIMIT = "limit"
        private const val QUERY_PARAM_TO_SYMBOL = "tsym"
        private const val QUERY_PARAM_FROM_SYMBOL = "fsym"
        private const val QUERY_PARAM_TO_SYMBOLS = "tsyms"
        private const val QUERY_PARAM_FROM_SYMBOLS = "fsyms"
        private const val PATH_PARAM_TIME_FRAME = "timeFrame"
        private const val QUERY_PARAM_CATEGORIES = "categories"
        private const val QUERY_PARAM_LANG = "lang"

        private const val CURRENCY = "USD"
    }
}