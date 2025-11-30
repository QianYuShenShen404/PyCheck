package com.example.codechecker.domain.repository

import com.example.codechecker.domain.model.User
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

    suspend fun updateUser(user: User)

    suspend fun updatePassword(userId: Long, newPasswordHash: String)
}
