package com.example.codechecker.domain.usecase

import com.example.codechecker.domain.repository.AdminSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for importing admin settings
 */
@Singleton
class ImportSettingsUseCase @Inject constructor(
    private val adminSettingsRepository: AdminSettingsRepository,
    private val auditLogger: AuditLogger
) {
    suspend operator fun invoke(
        json: String,
        adminUserId: Long
    ): Result<Unit> {
        return try {
            adminSettingsRepository.importSettings(json, adminUserId)

            auditLogger.log(
                adminUserId = adminUserId,
                action = com.example.codechecker.domain.model.AuditAction.SETTINGS_UPDATE,
                targetType = com.example.codechecker.domain.model.AuditTargetType.SYSTEM,
                result = com.example.codechecker.domain.model.AuditResult.SUCCESS,
                details = "Imported admin settings from JSON"
            )

            Result.success(Unit)
        } catch (e: Exception) {
            auditLogger.logError(
                adminUserId = adminUserId,
                action = com.example.codechecker.domain.model.AuditAction.SETTINGS_UPDATE,
                targetType = com.example.codechecker.domain.model.AuditTargetType.SYSTEM,
                error = e,
                details = "Failed to import admin settings"
            )
            Result.failure(e)
        }
    }
}
