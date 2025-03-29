package com.grebnev.cryptoprice.domain.entity

data class News(
    val id: Long,
    val publishedOn: String,
    val imageUrl: String,
    val sourceUrl: String,
    val title: String,
)