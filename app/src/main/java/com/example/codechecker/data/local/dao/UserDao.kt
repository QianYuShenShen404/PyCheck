package com.example.codechecker.data.local.dao

import androidx.room.*
import com.example.codechecker.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for User entity
 */
@Dao
interface UserDao {

    /**
     * Insert a user
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    /**
     * Update a user
     */
    @Update
    suspend fun updateUser(user: UserEntity)

    /**
     * Delete a user
     */
    @Delete
    suspend fun deleteUser(user: UserEntity)

    /**
     * Get user by ID
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Long): UserEntity?

    /**
     * Get user by username
     */
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): UserEntity?

    /**
     * Get all users as Flow
     */
    @Query("SELECT * FROM users")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    /**
     * Get all users
     */
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>

    /**
     * Get all active users
     */
    @Query("SELECT * FROM users WHERE isActive = 1")
    suspend fun getAllActiveUsers(): List<UserEntity>

    /**
     * Get users by role
     */
    @Query("SELECT * FROM users WHERE role = :role")
    suspend fun getUsersByRole(role: String): List<UserEntity>

    /**
     * Get users by status
     */
    @Query("SELECT * FROM users WHERE status = :status")
    suspend fun getUsersByStatus(status: String): List<UserEntity>

    /**
     * Update user status
     */
    @Query("UPDATE users SET status = :status WHERE id = :userId")
    suspend fun updateUserStatus(userId: Long, status: String)

    /**
     * Update user active flag
     */
    @Query("UPDATE users SET isActive = :isActive WHERE id = :userId")
    suspend fun updateUserActiveFlag(userId: Long, isActive: Boolean)

    /**
     * Update user role
     */
    @Query("UPDATE users SET role = :role WHERE id = :userId")
    suspend fun updateUserRole(userId: Long, role: String)

    /**
     * Update user password hash
     */
    @Query("UPDATE users SET passwordHash = :passwordHash WHERE id = :userId")
    suspend fun updateUserPasswordHash(userId: Long, passwordHash: String)

    /**
     * Get user count
     */
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    /**
     * Get active user count
     */
    @Query("SELECT COUNT(*) FROM users WHERE isActive = 1")
    suspend fun getActiveUserCount(): Int

    /**
     * Get user count by role
     */
    @Query("SELECT COUNT(*) FROM users WHERE role = :role")
    suspend fun getUserCountByRole(role: String): Int

    /**
     * Search users by username or display name
     */
    @Query("SELECT * FROM users WHERE username LIKE '%' || :query || '%' OR displayName LIKE '%' || :query || '%'")
    suspend fun searchUsers(query: String): List<UserEntity>

    /**
     * Get users with pagination
     */
    @Query("SELECT * FROM users ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getUsersPaged(limit: Int, offset: Int): List<UserEntity>
}
