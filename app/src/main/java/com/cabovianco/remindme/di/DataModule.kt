package com.cabovianco.remindme.di

import android.content.Context
import androidx.room.Room
import com.cabovianco.remindme.data.local.AppDatabase
import com.cabovianco.remindme.data.local.migration.MIGRATION_1_2
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
    private val MIGRATIONS = arrayOf(
        MIGRATION_1_2
    )

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        )
            .addMigrations(*MIGRATIONS)
            .build()

    @Provides
    fun provideReminderDao(appDatabase: AppDatabase) =
        appDatabase.reminderDao()

    @Provides
    fun provideTagDao(appDatabase: AppDatabase) =
        appDatabase.tagDao()
}
