package com.grebnev.cryptoprice.presentation.coinitem.terminal.bars

enum class TimeFrame(
    val value: String,
) {
    DAILY("histoday"),
    HOURLY("histohour"),
    MINUTE("histominute"),
}