package com.example.codechecker.domain.usecase

import com.example.codechecker.domain.model.User
import com.example.codechecker.util.CryptoUtils
import com.example.codechecker.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val cryptoUtils: CryptoUtils
) {
    suspend fun registerUser(
        username: String,
        password: String,
        displayName: String,
        role: String,
        passwordHash: (String) -> String
    ): Result<User> {
        return try {
            val existingUser = userRepository.getUserByUsername(username)
            if (existingUser != null) {
                return Result.failure(Exception("用户名已存在"))
            }
            val hashedPassword = passwordHash(password)
            val user = User(
                username = username,
                displayName = displayName,
                role = com.example.codechecker.domain.model.Role.fromValue(role),
                createdAt = System.currentTimeMillis()
            )
            val userId = userRepository.registerUser(user, hashedPassword)
            val savedUser = user.copy(id = userId)
            Result.success(savedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(
        username: String,
        password: String,
        passwordHash: (String) -> String
    ): Result<User> {
        return try {
            val hashedPassword = passwordHash(password)
            val user = userRepository.login(username, hashedPassword)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("用户名或密码错误"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changePassword(
        userId: Long,
        oldPassword: String,
        newPassword: String
    ): Result<Unit> {
        return try {
            if (newPassword.length < 8) {
                return Result.failure(Exception("密码长度至少8位"))
            }
            val hasLetter = newPassword.any { it.isLetter() }
            val hasDigit = newPassword.any { it.isDigit() }
            if (!(hasLetter && hasDigit)) {
                return Result.failure(Exception("密码需包含字母和数字"))
            }

            val user = userRepository.getUserById(userId) ?: return Result.failure(Exception("用户不存在"))
            val oldHash = cryptoUtils.hashPassword(oldPassword)
            val ok = userRepository.login(user.username, oldHash) != null
            if (!ok) {
                return Result.failure(Exception("原密码错误"))
            }
            val newHash = cryptoUtils.hashPassword(newPassword)
            userRepository.updatePassword(userId, newHash)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyPassword(userId: Long, oldPassword: String): Result<Unit> {
        return try {
            val user = userRepository.getUserById(userId) ?: return Result.failure(Exception("用户不存在"))
            val oldHash = cryptoUtils.hashPassword(oldPassword)
            val ok = userRepository.login(user.username, oldHash) != null
            if (!ok) Result.failure(Exception("原密码错误")) else Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
