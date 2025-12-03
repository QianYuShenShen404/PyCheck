package com.example.codechecker.data.mapper

import com.example.codechecker.data.local.entity.UserEntity
import com.example.codechecker.domain.model.Role
import com.example.codechecker.domain.model.User
import com.example.codechecker.domain.model.UserStatus

/**
 * Mapper for User entity and domain model
 */
object UserMapper {

    /**
     * Convert entity to domain model
     */
    fun toDomain(entity: UserEntity): User {
        return User(
            id = entity.id,
            username = entity.username,
            displayName = entity.displayName,
            role = Role.fromValue(entity.role),
            createdAt = entity.createdAt,
            isActive = entity.isActive,
            status = UserStatus.fromValue(entity.status)
        )
    }

    /**
     * Convert domain model to entity
     */
    fun toEntity(domain: User, passwordHash: String = ""): UserEntity {
        return UserEntity(
            id = domain.id,
            username = domain.username,
            passwordHash = passwordHash,
            displayName = domain.displayName,
            role = domain.role.value,
            createdAt = domain.createdAt,
            isActive = domain.isActive,
            status = domain.status.value
        )
    }

    /**
     * Convert list of entities to domain models
     */
    fun toDomainList(entities: List<UserEntity>): List<User> {
        return entities.map { toDomain(it) }
    }
}
