package com.example.codechecker.domain.usecase

import com.example.codechecker.domain.model.UserStatus
import com.example.codechecker.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for enabling a disabled user
 */
@Singleton
class EnableUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val auditLogger: AuditLogger
) {
    suspend operator fun invoke(
        userId: Long,
        adminUserId: Long
    ): Result<Unit> {
        return try {
            // Verify user exists
            val existingUser = userRepository.getUserById(userId)
            if (existingUser == null) {
                return Result.failure(Exception("用户不存在"))
            }

            // Check if already active
            if (existingUser.status == UserStatus.ACTIVE) {
                return Result.failure(Exception("用户已是活跃状态"))
            }

            // Enable user
            userRepository.updateUserStatus(userId, UserStatus.ACTIVE)

            // Log audit event
            auditLogger.log(
                adminUserId = adminUserId,
                action = com.example.codechecker.domain.model.AuditAction.USER_ENABLE,
                targetType = com.example.codechecker.domain.model.AuditTargetType.USER,
                targetId = userId.toString(),
                result = com.example.codechecker.domain.model.AuditResult.SUCCESS,
                details = "Enabled user: ${existingUser.username}"
            )

            Result.success(Unit)
        } catch (e: Exception) {
            auditLogger.logError(
                adminUserId = adminUserId,
                action = com.example.codechecker.domain.model.AuditAction.USER_ENABLE,
                targetType = com.example.codechecker.domain.model.AuditTargetType.USER,
                error = e,
                details = "Failed to enable user ID: $userId"
            )
            Result.failure(e)
        }
    }
}
