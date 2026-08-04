package com.cabovianco.remindme.data.repository

import com.cabovianco.remindme.data.local.source.UserPreferencesDataSource
import com.cabovianco.remindme.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OnboardingRepositoryImpl @Inject constructor(
    private val dataSource: UserPreferencesDataSource
) : OnboardingRepository {
    override val showWelcomeScreen: Flow<Boolean> = dataSource.showWelcomeScreen

    override suspend fun setShowWelcomeScreen(show: Boolean) {
        dataSource.setShowWelcomeScreen(show)
    }
}
