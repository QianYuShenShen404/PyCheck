package com.example.codechecker.domain.usecase

import com.example.codechecker.domain.model.User
import com.example.codechecker.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for creating a new user
 */
@Singleton
class CreateUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val auditLogger: AuditLogger
) {
    suspend operator fun invoke(
        username: String,
        passwordHash: String,
        displayName: String,
        role: com.example.codechecker.domain.model.Role,
        adminUserId: Long
    ): Result<User> {
        return try {
            // Check if username already exists
            val existingUser = userRepository.getUserByUsername(username)
            if (existingUser != null) {
                return Result.failure(Exception("用户名已存在"))
            }

            // Create new user
            val newUser = User(
                username = username,
                displayName = displayName,
                role = role,
                createdAt = System.currentTimeMillis(),
                isActive = true,
                status = com.example.codechecker.domain.model.UserStatus.ACTIVE
            )

            val userId = userRepository.registerUser(newUser, passwordHash)
            val savedUser = newUser.copy(id = userId)

            // Log audit event
            auditLogger.log(
                adminUserId = adminUserId,
                action = com.example.codechecker.domain.model.AuditAction.USER_CREATE,
                targetType = com.example.codechecker.domain.model.AuditTargetType.USER,
                targetId = userId.toString(),
                result = com.example.codechecker.domain.model.AuditResult.SUCCESS,
                details = "Created user: $username with role: ${role.value}"
            )

            Result.success(savedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
