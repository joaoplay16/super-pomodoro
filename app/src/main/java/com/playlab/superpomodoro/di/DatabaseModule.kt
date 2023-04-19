package com.playlab.superpomodoro.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.firebase.FirebaseApp
import com.playlab.superpomodoro.data.preferences.PreferencesDataStore
import com.playlab.superpomodoro.repository.DefaultPreferencesRepository
import com.playlab.superpomodoro.repository.PreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext appContext: Context
    ): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = {
                appContext.preferencesDataStoreFile("settings")
        }
    )

    @Provides
    @Singleton
    fun providePreferencesRepository(
        dataStore: PreferencesDataStore
    ): PreferencesRepository =
        DefaultPreferencesRepository(dataStore)

    @Provides
    @Singleton
    fun provideFirebaseInstance(
        @ApplicationContext context: Context
    ) : FirebaseApp? =
        FirebaseApp.initializeApp(context)
}