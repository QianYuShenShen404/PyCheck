package com.example.codechecker.domain.usecase

import com.example.codechecker.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for resetting user password
 */
@Singleton
class ResetPasswordUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val auditLogger: AuditLogger
) {
    suspend operator fun invoke(
        userId: Long,
        newPasswordHash: String,
        adminUserId: Long
    ): Result<Unit> {
        return try {
            // Verify user exists
            val existingUser = userRepository.getUserById(userId)
            if (existingUser == null) {
                return Result.failure(Exception("用户不存在"))
            }

            // Reset password
            userRepository.resetPassword(userId, newPasswordHash)

            // Log audit event
            auditLogger.log(
                adminUserId = adminUserId,
                action = com.example.codechecker.domain.model.AuditAction.PASSWORD_RESET,
                targetType = com.example.codechecker.domain.model.AuditTargetType.USER,
                targetId = userId.toString(),
                result = com.example.codechecker.domain.model.AuditResult.SUCCESS,
                details = "Reset password for user: ${existingUser.username}"
            )

            Result.success(Unit)
        } catch (e: Exception) {
            auditLogger.logError(
                adminUserId = adminUserId,
                action = com.example.codechecker.domain.model.AuditAction.PASSWORD_RESET,
                targetType = com.example.codechecker.domain.model.AuditTargetType.USER,
                error = e,
                details = "Failed to reset password for user ID: $userId"
            )
            Result.failure(e)
        }
    }
}
