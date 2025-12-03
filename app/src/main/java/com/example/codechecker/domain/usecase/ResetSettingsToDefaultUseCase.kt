package com.example.codechecker.domain.usecase

import com.example.codechecker.domain.repository.AdminSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for resetting admin settings to defaults
 */
@Singleton
class ResetSettingsToDefaultUseCase @Inject constructor(
    private val adminSettingsRepository: AdminSettingsRepository,
    private val auditLogger: AuditLogger
) {
    suspend operator fun invoke(adminUserId: Long): Result<Unit> {
        return try {
            adminSettingsRepository.resetToDefaults(adminUserId)

            auditLogger.log(
                adminUserId = adminUserId,
                action = com.example.codechecker.domain.model.AuditAction.SETTINGS_UPDATE,
                targetType = com.example.codechecker.domain.model.AuditTargetType.SYSTEM,
                result = com.example.codechecker.domain.model.AuditResult.SUCCESS,
                details = "Reset admin settings to defaults"
            )

            Result.success(Unit)
        } catch (e: Exception) {
            auditLogger.logError(
                adminUserId = adminUserId,
                action = com.example.codechecker.domain.model.AuditAction.SETTINGS_UPDATE,
                targetType = com.example.codechecker.domain.model.AuditTargetType.SYSTEM,
                error = e,
                details = "Failed to reset admin settings to defaults"
            )
            Result.failure(e)
        }
    }
}
