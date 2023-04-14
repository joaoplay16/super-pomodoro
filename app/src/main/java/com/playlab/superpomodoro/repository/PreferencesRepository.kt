package com.playlab.superpomodoro.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository{
    
    fun isSoundAllowed(): Flow<Boolean?>
    suspend fun allowSound(allowed: Boolean)

    fun isVibrationAllowed(): Flow<Boolean?>
    suspend fun allowVibration(allowed: Boolean)

    fun pomodoroDuration(): Flow<Int?>
    suspend fun setPomodoroDuration(duration: Int)

    fun shortBreakDuration(): Flow<Int?>
    suspend fun setShortBreakDuration(duration: Int)

    fun longBreakDuration(): Flow<Int?>
    suspend fun setLongBreakDuration(duration: Int)
}