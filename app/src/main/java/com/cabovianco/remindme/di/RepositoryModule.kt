package com.cabovianco.remindme.di

import com.cabovianco.remindme.data.repository.ReminderRepositoryImpl
import com.cabovianco.remindme.data.repository.TagRepositoryImpl
import com.cabovianco.remindme.domain.repository.ReminderRepository
import com.cabovianco.remindme.domain.repository.TagRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindReminderRepository(
        impl: ReminderRepositoryImpl
    ): ReminderRepository

    @Binds
    @Singleton
    abstract fun bindTagRepository(
        impl: TagRepositoryImpl
    ): TagRepository
}
