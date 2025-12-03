package com.example.codechecker.domain.repository

import com.example.codechecker.domain.model.Report
import com.example.codechecker.domain.model.Similarity
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Report operations
 */
interface ReportRepository {

    /**
     * Create a new report
     */
    suspend fun createReport(report: Report): Long

    /**
     * Update a report
     */
    suspend fun updateReport(report: Report)

    /**
     * Delete a report
     */
    suspend fun deleteReport(report: Report)

    /**
     * Get report by ID
     */
    suspend fun getReportById(reportId: Long): Report?

    /**
     * Get reports by assignment
     */
    fun getReportsByAssignmentFlow(assignmentId: Long): Flow<List<Report>>

    /**
     * Get reports by assignment (suspend version)
     */
    suspend fun getReportsByAssignment(assignmentId: Long): List<Report>

    /**
     * Get all reports
     */
    fun getAllReportsFlow(): Flow<List<Report>>

    /**
     * Save similarity results
     */
    suspend fun saveSimilarities(similarities: List<Similarity>)

    /**
     * Create a single similarity
     */
    suspend fun createSimilarity(similarity: Similarity): Long

    /**
     * Get similarities by report
     */
    fun getSimilaritiesByReportFlow(reportId: Long): Flow<List<Similarity>>

    /**
     * Get similarities by report (suspend version)
     */
    suspend fun getSimilaritiesByReport(reportId: Long): List<Similarity>

    /**
     * Get high similarities by report
     */
    fun getHighSimilaritiesFlow(reportId: Long, threshold: Float = 60f): Flow<List<Similarity>>

    /**
     * Get similarity by ID
     */
    suspend fun getSimilarityById(similarityId: Long): Similarity?

    suspend fun updateSimilarity(similarity: Similarity)
}
