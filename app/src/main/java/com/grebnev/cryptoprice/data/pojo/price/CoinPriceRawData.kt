package com.grebnev.cryptoprice.data.pojo.price

import com.google.gson.JsonObject
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

data class CoinPriceRawData(
    @SerializedName("RAW")
    @Expose
    private val coinPriceJsonObject: JsonObject? = null
)