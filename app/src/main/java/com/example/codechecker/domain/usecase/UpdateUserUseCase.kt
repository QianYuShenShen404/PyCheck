package com.example.codechecker.domain.usecase

import com.example.codechecker.domain.model.User
import com.example.codechecker.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for updating user information
 */
@Singleton
class UpdateUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val auditLogger: AuditLogger
) {
    suspend operator fun invoke(
        user: User,
        adminUserId: Long
    ): Result<User> {
        return try {
            // Verify user exists
            val existingUser = userRepository.getUserById(user.id)
            if (existingUser == null) {
                return Result.failure(Exception("用户不存在"))
            }

            // Update user
            userRepository.updateUser(user)

            // Log audit event
            auditLogger.log(
                adminUserId = adminUserId,
                action = com.example.codechecker.domain.model.AuditAction.USER_UPDATE,
                targetType = com.example.codechecker.domain.model.AuditTargetType.USER,
                targetId = user.id.toString(),
                result = com.example.codechecker.domain.model.AuditResult.SUCCESS,
                details = "Updated user: ${user.username}"
            )

            Result.success(user)
        } catch (e: Exception) {
            auditLogger.logError(
                adminUserId = adminUserId,
                action = com.example.codechecker.domain.model.AuditAction.USER_UPDATE,
                targetType = com.example.codechecker.domain.model.AuditTargetType.USER,
                error = e,
                details = "Failed to update user: ${user.username}"
            )
            Result.failure(e)
        }
    }
}
