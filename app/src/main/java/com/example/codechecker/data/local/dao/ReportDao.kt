package com.example.codechecker.data.local.dao

import androidx.room.*
import com.example.codechecker.data.local.entity.ReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity): Long

    @Update
    suspend fun updateReport(report: ReportEntity)

    @Delete
    suspend fun deleteReport(report: ReportEntity)

    @Query("SELECT * FROM plagiarism_reports WHERE id = :reportId")
    suspend fun getReportById(reportId: Long): ReportEntity?

    @Query("SELECT * FROM plagiarism_reports WHERE assignment_id = :assignmentId ORDER BY created_at DESC")
    fun getReportsByAssignmentFlow(assignmentId: Long): Flow<List<ReportEntity>>

    @Query("SELECT * FROM plagiarism_reports ORDER BY created_at DESC")
    fun getAllReportsFlow(): Flow<List<ReportEntity>>
}
