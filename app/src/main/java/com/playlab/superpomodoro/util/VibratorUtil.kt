package com.playlab.superpomodoro.util

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresPermission

object VibratorUtil {
    @RequiresPermission(android.Manifest.permission.VIBRATE)
    fun vibrate(context: Context, duration: Long, amplitude: Int = -1) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // Use VibrationEffect para controlar a duração e a intensidade da vibração
                val effect = VibrationEffect.createOneShot(duration, amplitude)
                vibrator.vibrate(effect)
            } else {
                vibrator.vibrate(duration)
            }
        }
    }
}
