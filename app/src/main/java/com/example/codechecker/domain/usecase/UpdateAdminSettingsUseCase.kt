package com.example.codechecker.domain.usecase

import com.example.codechecker.domain.model.AdminSettings
import com.example.codechecker.domain.repository.AdminSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for updating admin settings
 */
@Singleton
class UpdateAdminSettingsUseCase @Inject constructor(
    private val adminSettingsRepository: AdminSettingsRepository,
    private val auditLogger: AuditLogger
) {
    suspend operator fun invoke(
        settings: AdminSettings,
        adminUserId: Long
    ): Result<Unit> {
        return try {
            // Validate settings
            val validationResult = validateSettings(settings)
            if (!validationResult.isValid) {
                return Result.failure(Exception("设置无效: ${validationResult.error}"))
            }

            // Update settings
            adminSettingsRepository.updateAdminSettings(settings, adminUserId)

            // Log audit event
            auditLogger.log(
                adminUserId = adminUserId,
                action = com.example.codechecker.domain.model.AuditAction.SETTINGS_UPDATE,
                targetType = com.example.codechecker.domain.model.AuditTargetType.SYSTEM,
                result = com.example.codechecker.domain.model.AuditResult.SUCCESS,
                details = "Updated admin settings"
            )

            Result.success(Unit)
        } catch (e: Exception) {
            auditLogger.logError(
                adminUserId = adminUserId,
                action = com.example.codechecker.domain.model.AuditAction.SETTINGS_UPDATE,
                targetType = com.example.codechecker.domain.model.AuditTargetType.SYSTEM,
                error = e,
                details = "Failed to update admin settings"
            )
            Result.failure(e)
        }
    }

    suspend fun updateIndividualSetting(
        key: String,
        value: String,
        type: com.example.codechecker.domain.model.SettingType,
        adminUserId: Long
    ): Result<Unit> {
        return try {
            adminSettingsRepository.updateSetting(key, value, type, adminUserId)

            auditLogger.log(
                adminUserId = adminUserId,
                action = com.example.codechecker.domain.model.AuditAction.SETTINGS_UPDATE,
                targetType = com.example.codechecker.domain.model.AuditTargetType.SYSTEM,
                targetId = key,
                result = com.example.codechecker.domain.model.AuditResult.SUCCESS,
                details = "Updated setting: $key = $value"
            )

            Result.success(Unit)
        } catch (e: Exception) {
            auditLogger.logError(
                adminUserId = adminUserId,
                action = com.example.codechecker.domain.model.AuditAction.SETTINGS_UPDATE,
                targetType = com.example.codechecker.domain.model.AuditTargetType.SYSTEM,
                error = e,
                details = "Failed to update setting: $key"
            )
            Result.failure(e)
        }
    }

    private data class ValidationResult(val isValid: Boolean, val error: String?)

    private fun validateSettings(settings: AdminSettings): ValidationResult {
        // Validate similarity threshold
        if (settings.similarityThreshold !in 0..100) {
            return ValidationResult(false, "相似度阈值必须在0-100之间")
        }

        // Validate retention days
        if (settings.reportRetentionDays < 0) {
            return ValidationResult(false, "报告保留天数不能为负数")
        }

        if (settings.submissionRetentionDays < 0) {
            return ValidationResult(false, "提交保留天数不能为负数")
        }

        // Validate max submissions
        if (settings.maxSubmissionsPerAssignment <= 0) {
            return ValidationResult(false, "最大提交数必须大于0")
        }

        return ValidationResult(true, null)
    }
}
