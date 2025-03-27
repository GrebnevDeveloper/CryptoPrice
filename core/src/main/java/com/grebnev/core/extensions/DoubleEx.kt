package com.grebnev.core.extensions

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs

fun Double.formatWithRoundAndDelimiter(decimalPlaces: Int = 2): String {
    val symbols =
        DecimalFormatSymbols(Locale.getDefault())

    val pattern =
        buildString {
            append("#,##0")
            if (decimalPlaces > 0) {
                append(".")
                repeat(decimalPlaces) { append("0") }
            }
        }

    val formatter = DecimalFormat(pattern, symbols)
    formatter.roundingMode = java.math.RoundingMode.HALF_UP
    return formatter.format(this)
}

fun Double.formatWithRoundAndSuffix(): String {
    val (divisor, suffix) =
        when {
            abs(this) >= 1_000_000_000 -> Pair(1_000_000_000.0, " B")
            abs(this) >= 1_000_000 -> Pair(1_000_000.0, " M")
            abs(this) >= 1_000 -> Pair(1_000.0, " K")
            else -> Pair(1.0, "")
        }

    val formattedNumber = this / divisor

    val symbols =
        DecimalFormatSymbols()

    val pattern =
        when {
            abs(formattedNumber) >= 100 -> "#,##0"
            else -> "#,##0.##"
        }

    val formatter = DecimalFormat(pattern, symbols)
    formatter.roundingMode = java.math.RoundingMode.HALF_UP

    return formatter.format(formattedNumber) + suffix
}