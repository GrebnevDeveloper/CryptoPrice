package com.grebnev.cryptoprice.domain.entity

import java.io.Serializable
import java.util.Calendar
import java.util.Date

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
                time = Date(this@Bar.time)
            }
        }
}