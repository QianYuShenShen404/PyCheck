package com.example.codechecker.data.local.dao

import androidx.room.*
import com.example.codechecker.data.local.entity.SubmissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubmissionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: SubmissionEntity): Long

    @Update
    suspend fun updateSubmission(submission: SubmissionEntity)

    @Delete
    suspend fun deleteSubmission(submission: SubmissionEntity)

    @Query("SELECT * FROM submissions WHERE id = :submissionId")
    suspend fun getSubmissionById(submissionId: Long): SubmissionEntity?

    @Query("SELECT * FROM submissions WHERE student_id = :studentId AND assignment_id = :assignmentId ORDER BY submitted_at DESC")
    fun getSubmissionsByStudentAndAssignmentFlow(
        studentId: Long,
        assignmentId: Long
    ): Flow<List<SubmissionEntity>>

    @Query("SELECT * FROM submissions WHERE student_id = :studentId ORDER BY submitted_at DESC")
    fun getSubmissionsByStudentFlow(studentId: Long): Flow<List<SubmissionEntity>>

    @Query("SELECT * FROM submissions WHERE assignment_id = :assignmentId ORDER BY submitted_at DESC")
    fun getSubmissionsByAssignmentFlow(assignmentId: Long): Flow<List<SubmissionEntity>>

    @Query("SELECT * FROM submissions WHERE assignment_id = :assignmentId")
    suspend fun getSubmissionsByAssignment(assignmentId: Long): List<SubmissionEntity>

    @Query("SELECT COUNT(*) FROM submissions WHERE assignment_id = :assignmentId")
    suspend fun getSubmissionCountByAssignment(assignmentId: Long): Int

    @Query("SELECT * FROM submissions WHERE student_id = :studentId ORDER BY submitted_at DESC")
    suspend fun getSubmissionsByUser(studentId: Long): List<SubmissionEntity>

    @Query("SELECT COUNT(DISTINCT student_id) FROM submissions WHERE assignment_id = :assignmentId")
    suspend fun getSubmittedStudentCountByAssignment(assignmentId: Long): Int

    @Query("SELECT EXISTS(SELECT 1 FROM submissions WHERE assignment_id = :assignmentId AND student_id = :studentId)")
    suspend fun hasStudentSubmittedAssignment(assignmentId: Long, studentId: Long): Boolean

    @Query(
        """
        SELECT
            s.id, s.student_id, s.assignment_id, s.file_name,
            s.code_content, s.code_hash, s.status, s.submitted_at,
            u.username AS student_number, u.displayName AS student_name
        FROM submissions s
        LEFT JOIN users u ON s.student_id = u.id
        WHERE s.assignment_id = :assignmentId
        ORDER BY s.submitted_at DESC
        """
    )
    suspend fun getSubmissionsByAssignmentWithStudentInfo(assignmentId: Long): List<SubmissionEntity>
}
