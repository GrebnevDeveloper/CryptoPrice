package com.grebnev.core.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun Long?.convertTimestampToTimeByPattern(
    pattern: String,
    timeZone: TimeZone = TimeZone.getDefault(),
): String {
    if (this == null) return ""
    val date = Date(this * 1000)
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    sdf.timeZone = timeZone
    return sdf.format(date)
}