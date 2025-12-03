package com.example.codechecker.domain.usecase

import com.example.codechecker.domain.repository.AdminSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for exporting admin settings
 */
@Singleton
class ExportSettingsUseCase @Inject constructor(
    private val adminSettingsRepository: AdminSettingsRepository,
    private val auditLogger: AuditLogger
) {
    suspend operator fun invoke(adminUserId: Long): Result<String> {
        return try {
            val json = adminSettingsRepository.exportSettings()

            auditLogger.log(
                adminUserId = adminUserId,
                action = com.example.codechecker.domain.model.AuditAction.SETTINGS_UPDATE,
                targetType = com.example.codechecker.domain.model.AuditTargetType.SYSTEM,
                result = com.example.codechecker.domain.model.AuditResult.SUCCESS,
                details = "Exported admin settings"
            )

            Result.success(json)
        } catch (e: Exception) {
            auditLogger.logError(
                adminUserId = adminUserId,
                action = com.example.codechecker.domain.model.AuditAction.SETTINGS_UPDATE,
                targetType = com.example.codechecker.domain.model.AuditTargetType.SYSTEM,
                error = e,
                details = "Failed to export admin settings"
            )
            Result.failure(e)
        }
    }
}
