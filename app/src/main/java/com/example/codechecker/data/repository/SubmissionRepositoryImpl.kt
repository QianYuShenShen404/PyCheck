package com.example.codechecker.data.repository

import com.example.codechecker.data.local.dao.SubmissionDao
import com.example.codechecker.data.mapper.SubmissionMapper
import com.example.codechecker.domain.model.Submission
import com.example.codechecker.domain.repository.SubmissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of SubmissionRepository
 */
@Singleton
class SubmissionRepositoryImpl @Inject constructor(
    private val submissionDao: SubmissionDao
) : SubmissionRepository {

    override suspend fun submitCode(submission: Submission): Long {
        val submissionEntity = SubmissionMapper.toEntity(submission)
        return submissionDao.insertSubmission(submissionEntity)
    }

    override suspend fun updateSubmission(submission: Submission) {
        val submissionEntity = SubmissionMapper.toEntity(submission)
        submissionDao.updateSubmission(submissionEntity)
    }

    override suspend fun deleteSubmission(submission: Submission) {
        val submissionEntity = SubmissionMapper.toEntity(submission)
        submissionDao.deleteSubmission(submissionEntity)
    }

    override suspend fun getSubmissionById(submissionId: Long): Submission? {
        val submissionEntity = submissionDao.getSubmissionById(submissionId) ?: return null
        return SubmissionMapper.toDomain(submissionEntity)
    }

    override fun getSubmissionsByStudentAndAssignmentFlow(
        studentId: Long,
        assignmentId: Long
    ): Flow<List<Submission>> {
        return submissionDao.getSubmissionsByStudentAndAssignmentFlow(studentId, assignmentId)
            .map { entities -> SubmissionMapper.toDomainList(entities) }
    }

    override fun getSubmissionsByStudentFlow(studentId: Long): Flow<List<Submission>> {
        return submissionDao.getSubmissionsByStudentFlow(studentId)
            .map { entities -> SubmissionMapper.toDomainList(entities) }
    }

    override fun getSubmissionsByAssignmentFlow(assignmentId: Long): Flow<List<Submission>> {
        return submissionDao.getSubmissionsByAssignmentFlow(assignmentId)
            .map { entities -> SubmissionMapper.toDomainList(entities) }
    }

    override suspend fun getAllSubmissionsByAssignment(assignmentId: Long): List<Submission> {
        val entities = submissionDao.getSubmissionsByAssignment(assignmentId)
        return SubmissionMapper.toDomainList(entities)
    }

    override suspend fun getSubmissionCountByAssignment(assignmentId: Long): Int {
        return submissionDao.getSubmissionCountByAssignment(assignmentId)
    }

    override suspend fun getSubmissionsByUser(userId: Long): List<Submission> {
        val entities = submissionDao.getSubmissionsByUser(userId)
        return SubmissionMapper.toDomainList(entities)
    }

    override suspend fun getSubmittedStudentCountByAssignment(assignmentId: Long): Int {
        return submissionDao.getSubmittedStudentCountByAssignment(assignmentId)
    }

    override suspend fun hasStudentSubmittedAssignment(studentId: Long, assignmentId: Long): Boolean {
        return submissionDao.hasStudentSubmittedAssignment(assignmentId, studentId)
    }
}
