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
}
