package com.playlab.superpomodoro.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository{
    
    fun isSoundAllowed(): Flow<Boolean?>
    suspend fun allowSound(allowed: Boolean)

    fun isVibrationAllowed(): Flow<Boolean?>
    suspend fun allowVibration(allowed: Boolean)

    fun pomodoroDuration(): Flow<Long?>
    suspend fun setPomodoroDuration(duration: Long)

    fun shortBreakDuration(): Flow<Long?>
    suspend fun setShortBreakDuration(duration: Long)

    fun longBreakDuration(): Flow<Long?>
    suspend fun setLongBreakDuration(duration: Long)
}