package com.grebnev.cryptoprice.data.mapper

import com.google.gson.Gson
import com.grebnev.cryptoprice.data.api.model.CoinDto
import com.grebnev.cryptoprice.data.api.model.CoinJsonContainerDto
import com.grebnev.cryptoprice.data.api.model.CoinNameListDto
import com.grebnev.cryptoprice.data.database.CoinDbModel
import com.grebnev.cryptoprice.domain.entity.Coin

class CoinMapper {
    fun mapDtoToDbModel(coinDto: CoinDto) = CoinDbModel(
        fromSymbol = coinDto.fromSymbol,
        toSymbol = coinDto.toSymbol,
        price = coinDto.price,
        lastUpdate = coinDto.lastUpdate,
        highDay = coinDto.highDay,
        lowDay = coinDto.lowDay,
        lastMarket = coinDto.lastMarket,
        imageUrl = coinDto.imageUrl
    )

    fun mapJsonContainerDtoToCoinDtoList(jsonContainer: CoinJsonContainerDto): List<CoinDto> {
        val result = mutableListOf<CoinDto>()
        val jsonObject = jsonContainer.json ?: return result
        val coinKeySet = jsonObject.keySet()
        for (coinKey in coinKeySet) {
            val currencyJson = jsonObject.getAsJsonObject(coinKey)
            val currencyKeySet = currencyJson.keySet()
            for (currencyKey in currencyKeySet) {
                val priceInfo = Gson().fromJson(
                    currencyJson.getAsJsonObject(currencyKey),
                    CoinDto::class.java
                )
                result.add(priceInfo)
            }
        }
        return result
    }

    fun mapNamesListToString(nameListDto: CoinNameListDto): String {
        return nameListDto.names?.map {
            it.coinNameDto?.name
        }?.joinToString(",").orEmpty()
    }

    fun mapDbModelToEntity(coinDbModel: CoinDbModel) = Coin(
        fromSymbol = coinDbModel.fromSymbol,
        toSymbol = coinDbModel.toSymbol,
        price = coinDbModel.price,
        lastUpdate = coinDbModel.lastUpdate,
        highDay = coinDbModel.highDay,
        lowDay = coinDbModel.lowDay,
        lastMarket = coinDbModel.lastMarket,
        imageUrl = coinDbModel.imageUrl
    )
}