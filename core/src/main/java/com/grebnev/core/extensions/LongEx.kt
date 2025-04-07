package com.grebnev.core.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private const val MILLISECONDS_IN_SECONDS = 1000L

fun Long?.convertTimestampToTimeByPattern(
    pattern: String,
    timeZone: TimeZone = TimeZone.getDefault(),
): String {
    if (this == null) return ""
    val date = Date(this * MILLISECONDS_IN_SECONDS)
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    sdf.timeZone = timeZone
    return sdf.format(date)
}