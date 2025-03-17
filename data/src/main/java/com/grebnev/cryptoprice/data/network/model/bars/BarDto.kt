package com.grebnev.cryptoprice.data.network.model.bars

import com.google.gson.annotations.SerializedName

data class BarDto(
    @SerializedName("high")
    val high: Double,
    @SerializedName("low")
    val low: Double,
    @SerializedName("open")
    val open: Double,
    @SerializedName("close")
    val close: Double,
    @SerializedName("time")
    val time: Long,
)