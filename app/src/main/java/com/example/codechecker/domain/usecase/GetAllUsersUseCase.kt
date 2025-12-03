package com.example.codechecker.domain.usecase

import com.example.codechecker.domain.model.User
import com.example.codechecker.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for getting all users
 */
@Singleton
class GetAllUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): List<User> {
        return userRepository.getAllUsers()
    }

    suspend fun getActiveUsers(): List<User> {
        return userRepository.getAllActiveUsers()
    }

    suspend fun getUserCount(): Int {
        return userRepository.getUserCount()
    }

    suspend fun getActiveUserCount(): Int {
        return userRepository.getActiveUserCount()
    }

    suspend fun getUserCountByRole(role: com.example.codechecker.domain.model.Role): Int {
        return userRepository.getUserCountByRole(role)
    }

    fun getAllUsersFlow(): Flow<List<User>> {
        return userRepository.getAllUsersFlow()
    }

    suspend fun searchUsers(query: String): List<User> {
        return userRepository.searchUsers(query)
    }

    suspend fun getUsersPaged(limit: Int, offset: Int): List<User> {
        return userRepository.getUsersPaged(limit, offset)
    }
}
