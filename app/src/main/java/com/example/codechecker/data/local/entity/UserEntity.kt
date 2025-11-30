package com.example.codechecker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User entity representing a registered user
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val username: String,
    
    val passwordHash: String,
    
    val displayName: String,
    
    val role: String, // "STUDENT" or "TEACHER"
    
    val createdAt: Long
)
