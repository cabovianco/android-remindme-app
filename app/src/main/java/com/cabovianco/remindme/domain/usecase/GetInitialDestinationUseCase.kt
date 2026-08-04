package com.cabovianco.remindme.domain.usecase

import com.cabovianco.remindme.domain.model.InitialDestination
import com.cabovianco.remindme.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetInitialDestinationUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
    private val shouldShowPermissionScreenUseCase: ShouldShowPermissionScreenUseCase,
) {
    operator fun invoke(): Flow<InitialDestination> =
        onboardingRepository.showWelcomeScreen.map { showWelcomeScreen ->
            when {
                showWelcomeScreen -> InitialDestination.Welcome
                shouldShowPermissionScreenUseCase() -> InitialDestination.Permission
                else -> InitialDestination.Main
            }
        }
}
