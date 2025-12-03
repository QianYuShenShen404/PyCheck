package com.example.codechecker.data.mapper

import com.example.codechecker.data.local.entity.AdminAuditLogEntity
import com.example.codechecker.domain.model.AdminAuditLog

/**
 * Mapper for AdminAuditLog entity and domain model
 */
object AdminAuditLogMapper {

    /**
     * Convert entity to domain model
     */
    fun toDomain(entity: AdminAuditLogEntity): AdminAuditLog {
        return AdminAuditLog(
            id = entity.id,
            adminUserId = entity.adminUserId,
            action = entity.action,
            targetType = entity.targetType,
            targetId = entity.targetId,
            timestamp = entity.timestamp,
            result = entity.result,
            details = entity.details
        )
    }

    /**
     * Convert domain model to entity
     */
    fun toEntity(domain: AdminAuditLog): AdminAuditLogEntity {
        return AdminAuditLogEntity(
            id = domain.id,
            adminUserId = domain.adminUserId,
            action = domain.action,
            targetType = domain.targetType,
            targetId = domain.targetId,
            timestamp = domain.timestamp,
            result = domain.result,
            details = domain.details
        )
    }

    /**
     * Convert list of entities to domain models
     */
    fun toDomainList(entities: List<AdminAuditLogEntity>): List<AdminAuditLog> {
        return entities.map { toDomain(it) }
    }
}
