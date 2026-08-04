package com.cabovianco.remindme.domain.repository

import kotlinx.coroutines.flow.Flow

interface OnboardingRepository {
    val showWelcomeScreen: Flow<Boolean>
    suspend fun setShowWelcomeScreen(show: Boolean)
}
