package com.cabovianco.remindme.di

import android.content.Context
import androidx.room.Room
import com.cabovianco.remindme.data.local.AppDatabase
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

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()

    @Provides
    fun provideReminderDao(appDatabase: AppDatabase) =
        appDatabase.reminderDao()
}
