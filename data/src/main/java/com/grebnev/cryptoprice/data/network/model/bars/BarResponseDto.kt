package com.grebnev.cryptoprice.data.network.model.bars

import com.google.gson.annotations.SerializedName

data class BarResponseDto(
    @SerializedName("Data")
    val responseData: List<BarDto>,
)