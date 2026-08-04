package com.cabovianco.remindme.domain.usecase

import com.cabovianco.remindme.domain.repository.PermissionRepository
import javax.inject.Inject

class ShouldShowPermissionScreenUseCase @Inject constructor(
    private val repository: PermissionRepository
) {
    operator fun invoke(): Boolean =
        !repository.hasNotificationPermission() || !repository.hasExactAlarmPermission()
}
