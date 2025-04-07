package com.grebnev.cryptoprice.data.network.model.bars

import com.google.gson.annotations.SerializedName

data class BarContainerDto(
    @SerializedName("Data")
    val containerData: BarResponseDto,
)