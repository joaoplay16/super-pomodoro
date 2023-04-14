package com.playlab.superpomodoro.repository

import com.playlab.superpomodoro.data.preferences.PreferencesDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultPreferencesRepository @Inject constructor(
    private val preferencesDataStore: PreferencesDataStore
): PreferencesRepository{

    override fun isSoundAllowed(): Flow<Boolean?> {
        return preferencesDataStore.isSoundAllowed
    }
    override suspend fun allowSound(allowed: Boolean) {
        preferencesDataStore.allowSound(allowed)
    }

    override fun isVibrationAllowed(): Flow<Boolean?> {
       return preferencesDataStore.isVibrationAllowed
    }

    override suspend fun allowVibration(allowed: Boolean) {
        preferencesDataStore.allowVibration(allowed)
    }

    override fun pomodoroDuration(): Flow<Int?> {
        return preferencesDataStore.pomodoroDuration
    }

    override suspend fun setPomodoroDuration(duration: Int) {
        preferencesDataStore.setPomodoroDuration(duration)
    }

    override fun shortBreakDuration(): Flow<Int?> {
       return preferencesDataStore.shortBreakDuration
    }

    override suspend fun setShortBreakDuration(duration: Int) {
        preferencesDataStore.setShortBreakDuration(duration)
    }

    override fun longBreakDuration(): Flow<Int?> {
        return preferencesDataStore.longBreakDuration
    }

    override suspend fun setLongBreakDuration(duration: Int) {
        preferencesDataStore.setLongBreakDuration(duration)
    }
}