package com.grebnev.cryptoprice.data.network.model.news

import com.google.gson.annotations.SerializedName

data class NewsDto(
    @SerializedName("id") val id: Long,
    @SerializedName("published_on") val publishedOn: Long,
    @SerializedName("imageurl") val imageUrl: String,
    @SerializedName("url") val sourceUrl: String,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
)