package com.playlab.superpomodoro.util

import android.util.Log
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.math.floor

object TimeUtil {
    private fun getFormattedTimeString(minute: Int = 0, second: Int = 0): String {
        return String.format(
            Locale.getDefault(),
            "%02d:%02d",
            minute, second
        )
    }
    
    fun getFormattedTimeString(millis: Long): String{
        val minute = floor((millis.toDouble() / (1000 * 60)) % 60).toLong()
        val second = floor((millis.toDouble() / 1000) % 60).toLong()

        return getFormattedTimeString( minute.toInt(), second.toInt() )
    }

    fun Long.toFormattedTimeString(): String {
        val date = ZonedDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
        return runCatching { DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(date) }
            .onFailure { Log.e("TIME_UTIL", "${it.message}") }
            .getOrNull()
            .orEmpty()
    }
}