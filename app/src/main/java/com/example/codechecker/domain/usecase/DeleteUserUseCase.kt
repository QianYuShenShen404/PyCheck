package com.example.codechecker.domain.usecase

import com.example.codechecker.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for soft deleting a user
 */
@Singleton
class DeleteUserUseCase @Inject constructor(
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

            // Soft delete user (set isActive to false)
            userRepository.deleteUser(userId)

            // Log audit event
            auditLogger.log(
                adminUserId = adminUserId,
                action = com.example.codechecker.domain.model.AuditAction.USER_DELETE,
                targetType = com.example.codechecker.domain.model.AuditTargetType.USER,
                targetId = userId.toString(),
                result = com.example.codechecker.domain.model.AuditResult.SUCCESS,
                details = "Soft deleted user: ${existingUser.username}"
            )

            Result.success(Unit)
        } catch (e: Exception) {
            auditLogger.logError(
                adminUserId = adminUserId,
                action = com.example.codechecker.domain.model.AuditAction.USER_DELETE,
                targetType = com.example.codechecker.domain.model.AuditTargetType.USER,
                error = e,
                details = "Failed to delete user ID: $userId"
            )
            Result.failure(e)
        }
    }
}
