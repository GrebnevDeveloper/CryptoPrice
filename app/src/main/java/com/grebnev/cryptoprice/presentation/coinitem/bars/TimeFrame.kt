package com.grebnev.cryptoprice.presentation.coinitem.bars

enum class TimeFrame(
    val value: String,
) {
    DAILY("histoday"),
    HOURLY("histohour"),
    MINUTE("histominute"),
}