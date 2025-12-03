package com.example.codechecker.domain.usecase

import com.example.codechecker.domain.model.Role
import com.example.codechecker.domain.model.User
import com.example.codechecker.domain.model.UserStatus
import com.example.codechecker.domain.repository.UserRepository
import com.example.codechecker.util.CryptoUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnsureAdminExistsUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val createUserUseCase: CreateUserUseCase,
    private val cryptoUtils: CryptoUtils
) {
    suspend fun ensure(): Result<Unit> {
        return try {
            val adminCount = userRepository.getUserCountByRole(Role.ADMIN)
            if (adminCount > 0) {
                return Result.success(Unit)
            }

            val username = "admin"
            val defaultPassword = "BuaaAdmin#2025!"
            val displayName = "系统管理员"
            val passwordHash = cryptoUtils.sha256(defaultPassword)

            val existing = userRepository.getUserByUsername(username)
            if (existing == null) {
                val result = createUserUseCase(
                    username = username,
                    passwordHash = passwordHash,
                    displayName = displayName,
                    role = Role.ADMIN,
                    adminUserId = 0
                )
                result.fold(
                    onSuccess = { Result.success(Unit) },
                    onFailure = { Result.failure(it) }
                )
            } else {
                val elevated = existing.copy(
                    role = Role.ADMIN,
                    isActive = true,
                    status = UserStatus.ACTIVE
                )
                userRepository.updateUser(elevated)
                userRepository.updatePassword(elevated.id, passwordHash)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

