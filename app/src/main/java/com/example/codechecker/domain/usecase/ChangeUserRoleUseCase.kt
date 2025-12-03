package com.example.codechecker.domain.usecase

import com.example.codechecker.domain.model.Role
import com.example.codechecker.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for changing user role
 */
@Singleton
class ChangeUserRoleUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val auditLogger: AuditLogger
) {
    suspend operator fun invoke(
        userId: Long,
        newRole: Role,
        adminUserId: Long
    ): Result<Role> {
        return try {
            // Verify user exists
            val existingUser = userRepository.getUserById(userId)
            if (existingUser == null) {
                return Result.failure(Exception("用户不存在"))
            }

            val oldRole = existingUser.role

            // Update user role
            userRepository.updateUserRole(userId, newRole)

            // Log audit event
            auditLogger.log(
                adminUserId = adminUserId,
                action = com.example.codechecker.domain.model.AuditAction.USER_ROLE_CHANGE,
                targetType = com.example.codechecker.domain.model.AuditTargetType.USER,
                targetId = userId.toString(),
                result = com.example.codechecker.domain.model.AuditResult.SUCCESS,
                details = "Changed role for ${existingUser.username}: ${oldRole.value} -> ${newRole.value}"
            )

            Result.success(newRole)
        } catch (e: Exception) {
            auditLogger.logError(
                adminUserId = adminUserId,
                action = com.example.codechecker.domain.model.AuditAction.USER_ROLE_CHANGE,
                targetType = com.example.codechecker.domain.model.AuditTargetType.USER,
                error = e,
                details = "Failed to change role for user ID: $userId to ${newRole.value}"
            )
            Result.failure(e)
        }
    }
}
