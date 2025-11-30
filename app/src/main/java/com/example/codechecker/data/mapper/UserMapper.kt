package com.example.codechecker.data.mapper

import com.example.codechecker.data.local.entity.UserEntity
import com.example.codechecker.domain.model.Role
import com.example.codechecker.domain.model.User

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
            createdAt = entity.createdAt
        )
    }

    /**
     * Convert domain model to entity
     */
    fun toEntity(domain: User): UserEntity {
        return UserEntity(
            id = domain.id,
            username = domain.username,
            passwordHash = "", // Should be set separately
            displayName = domain.displayName,
            role = domain.role.value,
            createdAt = domain.createdAt
        )
    }

    /**
     * Convert list of entities to domain models
     */
    fun toDomainList(entities: List<UserEntity>): List<User> {
        return entities.map { toDomain(it) }
    }
}
