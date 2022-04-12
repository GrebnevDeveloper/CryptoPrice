package com.grebnev.cryptoprice.data.pojo.info

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

data class CoinInfoLisOfData (
    @SerializedName("Data")
    @Expose
    val data: List<Datum>? = null
)