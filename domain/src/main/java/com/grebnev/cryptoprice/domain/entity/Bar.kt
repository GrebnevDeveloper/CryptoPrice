package com.grebnev.cryptoprice.domain.entity

data class Bar(
    val high: Double,
    val low: Double,
    val open: Double,
    val close: Double,
    val time: Long,
)