package com.example.codechecker.domain.model

/**
 * Domain model for User
 */
data class User(
    val id: Long = 0,
    val username: String,
    val displayName: String,
    val role: Role,
    val createdAt: Long,
    val isActive: Boolean = true,
    val status: UserStatus = UserStatus.ACTIVE
)

/**
 * User role enum
 */
enum class Role(val value: String) {
    STUDENT("STUDENT"),
    TEACHER("TEACHER"),
    ADMIN("ADMIN");

    companion object {
        fun fromValue(value: String): Role {
            val normalized = value.uppercase()
            return values().find { it.value == normalized } ?: STUDENT
        }
    }
}

/**
 * User status enum
 */
enum class UserStatus(val value: String) {
    ACTIVE("ACTIVE"),
    DISABLED("DISABLED");

    companion object {
        fun fromValue(value: String): UserStatus {
            val normalized = value.uppercase()
            return values().find { it.value == normalized } ?: ACTIVE
        }
    }
}
