package com.grebnev.cryptoprice.data.network.model.coin

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CoinNameContainerDto(
    @SerializedName("CoinInfo")
    @Expose
    val coinNameDto: CoinNameDto? = null,
)