package com.example.codechecker.domain.repository

import com.example.codechecker.domain.model.User
import com.example.codechecker.domain.model.UserStatus
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for User operations
 */
interface UserRepository {

    /**
     * Register a new user
     */
    suspend fun registerUser(user: User, passwordHash: String): Long

    /**
     * Login user
     */
    suspend fun login(username: String, passwordHash: String): User?

    /**
     * Get user by ID
     */
    suspend fun getUserById(userId: Long): User?

    /**
     * Get user by username
     */
    suspend fun getUserByUsername(username: String): User?

    /**
     * Get all users as Flow
     */
    fun getAllUsersFlow(): Flow<List<User>>

    /**
     * Update user information
     */
    suspend fun updateUser(user: User)

    /**
     * Update user password
     */
    suspend fun updatePassword(userId: Long, newPasswordHash: String)

    // Admin operations

    /**
     * Get all users (for admin)
     */
    suspend fun getAllUsers(): List<User>

    /**
     * Get all active users
     */
    suspend fun getAllActiveUsers(): List<User>

    /**
     * Update user status (ACTIVE/DISABLED)
     */
    suspend fun updateUserStatus(userId: Long, status: UserStatus)

    /**
     * Update user active flag (soft delete)
     */
    suspend fun updateUserActiveFlag(userId: Long, isActive: Boolean)

    /**
     * Delete user (soft delete)
     */
    suspend fun deleteUser(userId: Long)

    /**
     * Update user role
     */
    suspend fun updateUserRole(userId: Long, role: com.example.codechecker.domain.model.Role)

    /**
     * Reset user password
     */
    suspend fun resetPassword(userId: Long, newPasswordHash: String)

    /**
     * Get user count
     */
    suspend fun getUserCount(): Int

    /**
     * Get active user count
     */
    suspend fun getActiveUserCount(): Int

    /**
     * Get user count by role
     */
    suspend fun getUserCountByRole(role: com.example.codechecker.domain.model.Role): Int

    /**
     * Search users
     */
    suspend fun searchUsers(query: String): List<User>

    /**
     * Get users with pagination
     */
    suspend fun getUsersPaged(limit: Int, offset: Int): List<User>
}
