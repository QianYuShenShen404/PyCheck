package com.example.codechecker.data.mapper

import com.example.codechecker.data.local.entity.SubmissionEntity
import com.example.codechecker.domain.model.Submission
import com.example.codechecker.domain.model.SubmissionStatus

/**
 * Mapper for Submission entity and domain model
 */
object SubmissionMapper {

    /**
     * Convert entity to domain model
     */
    fun toDomain(entity: SubmissionEntity): Submission {
        return Submission(
            id = entity.id,
            studentId = entity.studentId,
            assignmentId = entity.assignmentId,
            fileName = entity.fileName,
            codeContent = entity.codeContent,
            codeHash = entity.codeHash,
            status = SubmissionStatus.fromValue(entity.status),
            submittedAt = entity.submittedAt
        )
    }

    /**
     * Convert domain model to entity
     */
    fun toEntity(domain: Submission): SubmissionEntity {
        return SubmissionEntity(
            id = domain.id,
            studentId = domain.studentId,
            assignmentId = domain.assignmentId,
            fileName = domain.fileName,
            codeContent = domain.codeContent,
            codeHash = domain.codeHash,
            status = domain.status.value,
            submittedAt = domain.submittedAt
        )
    }

    /**
     * Convert list of entities to domain models
     */
    fun toDomainList(entities: List<SubmissionEntity>): List<Submission> {
        return entities.map { toDomain(it) }
    }
}
