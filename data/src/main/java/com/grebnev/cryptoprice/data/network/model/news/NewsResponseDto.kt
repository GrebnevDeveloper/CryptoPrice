package com.grebnev.cryptoprice.data.network.model.news

import com.google.gson.annotations.SerializedName

data class NewsResponseDto(
    @SerializedName("Data") val responseData: List<NewsDto>,
)