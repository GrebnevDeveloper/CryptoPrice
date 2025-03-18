package com.grebnev.cryptoprice.domain.entity

import java.io.Serializable
import java.util.Calendar

data class Bar(
    val high: Double,
    val low: Double,
    val open: Double,
    val close: Double,
    val time: Long,
) : Serializable {
    val calendar: Calendar
        get() {
            return Calendar.getInstance().apply {
                timeInMillis = this@Bar.time * 1000
            }
        }
}