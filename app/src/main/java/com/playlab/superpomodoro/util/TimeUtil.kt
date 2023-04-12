package com.playlab.superpomodoro.util

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
}