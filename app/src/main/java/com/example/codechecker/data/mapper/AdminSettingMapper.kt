package com.example.codechecker.data.mapper

import com.example.codechecker.data.local.entity.AdminSettingEntity
import com.example.codechecker.domain.model.AdminSetting
import com.example.codechecker.domain.model.SettingType

/**
 * Mapper for AdminSetting entity and domain model
 */
object AdminSettingMapper {

    /**
     * Convert entity to domain model
     */
    fun toDomain(entity: AdminSettingEntity): AdminSetting {
        return AdminSetting(
            key = entity.key,
            value = entity.value,
            type = SettingType.fromValue(entity.type),
            updatedAt = entity.updatedAt,
            updatedBy = entity.updatedBy
        )
    }

    /**
     * Convert domain model to entity
     */
    fun toEntity(domain: AdminSetting): AdminSettingEntity {
        return AdminSettingEntity(
            key = domain.key,
            value = domain.value,
            type = domain.type.value,
            updatedAt = domain.updatedAt,
            updatedBy = domain.updatedBy
        )
    }

    /**
     * Convert list of entities to domain models
     */
    fun toDomainList(entities: List<AdminSettingEntity>): List<AdminSetting> {
        return entities.map { toDomain(it) }
    }

    /**
     * Convert list of domain models to entities
     */
    fun toEntityList(domains: List<AdminSetting>): List<AdminSettingEntity> {
        return domains.map { toEntity(it) }
    }
}
