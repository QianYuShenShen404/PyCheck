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
}
