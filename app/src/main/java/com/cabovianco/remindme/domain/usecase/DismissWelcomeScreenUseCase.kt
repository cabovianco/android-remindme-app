package com.cabovianco.remindme.domain.usecase

import com.cabovianco.remindme.domain.repository.OnboardingRepository
import javax.inject.Inject

class DismissWelcomeScreenUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) {
    suspend operator fun invoke() {
        onboardingRepository.setShowWelcomeScreen(false)
    }
}
