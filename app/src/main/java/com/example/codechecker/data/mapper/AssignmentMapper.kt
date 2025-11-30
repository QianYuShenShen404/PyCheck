package com.example.codechecker.data.mapper

import com.example.codechecker.data.local.entity.AssignmentEntity
import com.example.codechecker.domain.model.Assignment
import com.example.codechecker.domain.model.AssignmentStatus
import com.example.codechecker.domain.model.PythonVersion
import com.example.codechecker.domain.model.SubmissionLimit

/**
 * Mapper for Assignment entity and domain model
 */
object AssignmentMapper {

    /**
     * Convert entity to domain model
     */
    fun toDomain(entity: AssignmentEntity): Assignment {
        return Assignment(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            teacherId = entity.teacherId,
            dueDate = entity.dueDate,
            submissionLimit = SubmissionLimit.fromValue(entity.submissionLimit),
            pythonVersion = PythonVersion.fromValue(entity.pythonVersion),
            status = AssignmentStatus.fromValue(entity.status),
            createdAt = entity.createdAt
        )
    }

    /**
     * Convert domain model to entity
     */
    fun toEntity(domain: Assignment): AssignmentEntity {
        return AssignmentEntity(
            id = domain.id,
            title = domain.title,
            description = domain.description,
            teacherId = domain.teacherId,
            dueDate = domain.dueDate,
            submissionLimit = domain.submissionLimit.value,
            pythonVersion = domain.pythonVersion.value,
            status = domain.status.value,
            createdAt = domain.createdAt
        )
    }

    /**
     * Convert list of entities to domain models
     */
    fun toDomainList(entities: List<AssignmentEntity>): List<Assignment> {
        return entities.map { toDomain(it) }
    }
}
