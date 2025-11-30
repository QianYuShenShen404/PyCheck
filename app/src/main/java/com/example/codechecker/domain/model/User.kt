package com.example.codechecker.domain.model

/**
 * Domain model for User
 */
data class User(
    val id: Long = 0,
    val username: String,
    val displayName: String,
    val role: Role,
    val createdAt: Long
)

/**
 * User role enum
 */
enum class Role(val value: String) {
    STUDENT("STUDENT"),
    TEACHER("TEACHER");

    companion object {
        fun fromValue(value: String): Role {
            val normalized = value.uppercase()
            return values().find { it.value == normalized } ?: STUDENT
        }
    }
}
