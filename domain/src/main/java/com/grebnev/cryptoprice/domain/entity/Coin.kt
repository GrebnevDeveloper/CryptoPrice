package com.grebnev.cryptoprice.domain.entity

data class Coin(
    val fromSymbol: String,
    val toSymbol: String?,
    val price: Double?,
    val lastUpdate: String,
    val highDay: Double?,
    val lowDay: Double?,
    val lastMarket: String?,
    val imageUrl: String,
    val mktCap: Double?,
    val changePct24Hour: Double?
)
