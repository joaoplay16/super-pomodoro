package com.playlab.superpomodoro.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesDataStore @Inject constructor(private val dataStore: DataStore<Preferences>) {
    companion object {
        val ALLOW_SOUND_KEY = booleanPreferencesKey("allow_sound")
        val ALLOW_VIBRATION_KEY = booleanPreferencesKey("allow_vibration")
        val POMODORO_DURATION_KEY = longPreferencesKey("pomodoro_duration")
        val SHORT_BREAK_DURATION_KEY = longPreferencesKey("short_break_duration")
        val LONG_BREAK_DURATION_KEY = longPreferencesKey("long_break_duration")
    }

    val isSoundAllowed: Flow<Boolean?> = dataStore.data
        .map { preferences ->
            preferences[ALLOW_SOUND_KEY]
        }

    suspend fun allowSound(allowed: Boolean) {
        dataStore.edit { preferences ->
            preferences[ALLOW_SOUND_KEY] = allowed
        }
    }

    val isVibrationAllowed: Flow<Boolean?> = dataStore.data
        .map { preferences ->
            preferences[ALLOW_VIBRATION_KEY]
        }

    suspend fun allowVibration(allowed: Boolean) {
        dataStore.edit { preferences ->
            preferences[ALLOW_VIBRATION_KEY] = allowed
        }
    }

    val pomodoroDuration: Flow<Long?> = dataStore.data
        .map { preferences ->
            preferences[POMODORO_DURATION_KEY]
        }

    suspend fun setPomodoroDuration(duration: Long) {
        dataStore.edit { preferences ->
            preferences[POMODORO_DURATION_KEY] = duration
        }
    }

    val shortBreakDuration: Flow<Long?> = dataStore.data
        .map { preferences ->
            preferences[SHORT_BREAK_DURATION_KEY]
        }

    suspend fun setShortBreakDuration(duration: Long) {
        dataStore.edit { preferences ->
            preferences[SHORT_BREAK_DURATION_KEY] = duration
        }
    }

    val longBreakDuration: Flow<Long?> = dataStore.data
        .map { preferences ->
            preferences[LONG_BREAK_DURATION_KEY]
        }

    suspend fun setLongBreakDuration(duration: Long) {
        dataStore.edit { preferences ->
            preferences[LONG_BREAK_DURATION_KEY] = duration
        }
    }
}