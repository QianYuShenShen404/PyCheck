package com.example.codechecker.data.mapper

import com.example.codechecker.data.local.entity.ReportEntity
import com.example.codechecker.domain.model.Report
import com.example.codechecker.domain.model.ReportStatus

/**
 * Mapper for Report entity and domain model
 */
object ReportMapper {

    /**
     * Convert entity to domain model
     */
    fun toDomain(entity: ReportEntity): Report {
        return Report(
            id = entity.id,
            assignmentId = entity.assignmentId,
            executorId = entity.executorId,
            status = ReportStatus.fromValue(entity.status),
            totalSubmissions = entity.totalSubmissions,
            totalPairs = entity.totalPairs,
            createdAt = entity.createdAt,
            completedAt = entity.completedAt
        )
    }

    /**
     * Convert domain model to entity
     */
    fun toEntity(domain: Report): ReportEntity {
        return ReportEntity(
            id = domain.id,
            assignmentId = domain.assignmentId,
            executorId = domain.executorId,
            status = domain.status.value,
            totalSubmissions = domain.totalSubmissions,
            totalPairs = domain.totalPairs,
            createdAt = domain.createdAt,
            completedAt = domain.completedAt
        )
    }

    /**
     * Convert list of entities to domain models
     */
    fun toDomainList(entities: List<ReportEntity>): List<Report> {
        return entities.map { toDomain(it) }
    }
}
