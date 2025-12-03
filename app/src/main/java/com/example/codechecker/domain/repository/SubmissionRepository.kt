package com.example.codechecker.domain.repository

import com.example.codechecker.domain.model.Submission
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Submission operations
 */
interface SubmissionRepository {

    /**
     * Submit code
     */
    suspend fun submitCode(submission: Submission): Long

    /**
     * Update submission
     */
    suspend fun updateSubmission(submission: Submission)

    /**
     * Delete submission
     */
    suspend fun deleteSubmission(submission: Submission)

    /**
     * Get submission by ID
     */
    suspend fun getSubmissionById(submissionId: Long): Submission?

    /**
     * Get submissions by student and assignment
     */
    fun getSubmissionsByStudentAndAssignmentFlow(
        studentId: Long,
        assignmentId: Long
    ): Flow<List<Submission>>

    /**
     * Get submissions by student
     */
    fun getSubmissionsByStudentFlow(studentId: Long): Flow<List<Submission>>

    /**
     * Get submissions by assignment
     */
    fun getSubmissionsByAssignmentFlow(assignmentId: Long): Flow<List<Submission>>

    /**
     * Get all submissions for an assignment
     */
    suspend fun getAllSubmissionsByAssignment(assignmentId: Long): List<Submission>

    /**
     * Get submission count for an assignment
     */
    suspend fun getSubmissionCountByAssignment(assignmentId: Long): Int

    /**
     * Get submissions by user
     */
    suspend fun getSubmissionsByUser(userId: Long): List<Submission>

    /**
     * Get distinct submitted students count for an assignment
     */
    suspend fun getSubmittedStudentCountByAssignment(assignmentId: Long): Int

    /**
     * Check if a student has at least one submission for an assignment
     */
    suspend fun hasStudentSubmittedAssignment(studentId: Long, assignmentId: Long): Boolean
}
