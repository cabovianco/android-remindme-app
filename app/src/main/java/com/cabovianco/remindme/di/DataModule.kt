package com.cabovianco.remindme.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.cabovianco.remindme.data.local.AppDatabase
import com.cabovianco.remindme.data.local.migration.MIGRATION_1_2
import com.cabovianco.remindme.data.local.migration.MIGRATION_2_3
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    private const val DATABASE_NAME = "app_database"
    private const val DATASTORE_NAME = "app_prefs"
    private val DATABASE_MIGRATIONS = arrayOf(
        MIGRATION_1_2,
        MIGRATION_2_3
    )

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        )
            .addMigrations(*DATABASE_MIGRATIONS)
            .build()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(DATASTORE_NAME) }
        )

    @Provides
    fun provideReminderDao(appDatabase: AppDatabase) =
        appDatabase.reminderDao()

    @Provides
    fun provideTagDao(appDatabase: AppDatabase) =
        appDatabase.tagDao()
}
