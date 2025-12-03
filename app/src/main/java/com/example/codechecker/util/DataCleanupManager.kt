package com.example.codechecker.util

import com.example.codechecker.domain.repository.AdminSettingsRepository
import com.example.codechecker.domain.repository.ReportRepository
import com.example.codechecker.domain.repository.SubmissionRepository
import com.example.codechecker.domain.usecase.AuditLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data cleanup manager
 * Manages retention policies and cleanup of old data
 */
@Singleton
class DataCleanupManager @Inject constructor(
    private val submissionRepository: SubmissionRepository,
    private val reportRepository: ReportRepository,
    private val adminSettingsRepository: AdminSettingsRepository,
    private val logger: Logger,
    private val auditLogger: AuditLogger
) {

    companion object {
        private const val DEFAULT_RETENTION_DAYS = 90L
    }

    /**
     * Check and cleanup old data using configured retention policies
     */
    fun checkAndCleanupData() {
        CoroutineScope(Dispatchers.IO).launch {
            performCleanup(adminUserId = 0)
        }
    }

    /**
     * Perform data cleanup with configured retention policies
     */
    suspend fun performCleanup(adminUserId: Long = 0): CleanupResult {
        logger.info("Starting data cleanup")

        try {
            val settings = adminSettingsRepository.getAdminSettings()
            val reportRetentionDays = settings.reportRetentionDays
            val submissionRetentionDays = settings.submissionRetentionDays

            logger.info("Using retention policies - Reports: $reportRetentionDays days, Submissions: $submissionRetentionDays days")

            val reportCleanupCount = cleanupOldReports(reportRetentionDays.toLong())
            val submissionCleanupCount = cleanupOldSubmissions(submissionRetentionDays.toLong())
            val similarityCleanupCount = cleanupOldSimilarities(reportRetentionDays.toLong())

            val result = CleanupResult(
                reportCleanupCount,
                submissionCleanupCount,
                similarityCleanupCount
            )

            logger.info("Data cleanup completed successfully: $result")

            if (adminUserId > 0) {
                auditLogger.log(
                    adminUserId = adminUserId,
                    action = com.example.codechecker.domain.model.AuditAction.DATA_CLEANUP,
                    targetType = com.example.codechecker.domain.model.AuditTargetType.SYSTEM,
                    result = com.example.codechecker.domain.model.AuditResult.SUCCESS,
                    details = "Cleaned up old data: $result"
                )
            }

            return result
        } catch (e: Exception) {
            logger.error("Data cleanup failed", e)
            if (adminUserId > 0) {
                auditLogger.logError(
                    adminUserId = adminUserId,
                    action = com.example.codechecker.domain.model.AuditAction.DATA_CLEANUP,
                    targetType = com.example.codechecker.domain.model.AuditTargetType.SYSTEM,
                    error = e,
                    details = "Failed to cleanup data"
                )
            }
            throw e
        }
    }

    /**
     * Cleanup old submissions
     */
    private suspend fun cleanupOldSubmissions(retentionDays: Long): Int {
        val cutoffDate = System.currentTimeMillis() - (retentionDays * 24 * 60 * 60 * 1000)
        logger.debug("Cleaning up submissions older than $retentionDays days")

        // TODO: Implement actual deletion when SubmissionRepository has deleteByDate
        // val deletedCount = submissionRepository.deleteSubmissionsOlderThan(cutoffDate)
        val deletedCount = 0

        logger.debug("Cleaned up $deletedCount old submissions")
        return deletedCount
    }

    /**
     * Cleanup old reports
     */
    private suspend fun cleanupOldReports(retentionDays: Long): Int {
        val cutoffDate = System.currentTimeMillis() - (retentionDays * 24 * 60 * 60 * 1000)
        logger.debug("Cleaning up reports older than $retentionDays days")

        // TODO: Implement actual deletion when ReportRepository has deleteByDate
        // val deletedCount = reportRepository.deleteReportsOlderThan(cutoffDate)
        val deletedCount = 0

        logger.debug("Cleaned up $deletedCount old reports")
        return deletedCount
    }

    /**
     * Cleanup old similarity data
     */
    private suspend fun cleanupOldSimilarities(retentionDays: Long): Int {
        val cutoffDate = System.currentTimeMillis() - (retentionDays * 24 * 60 * 60 * 1000)
        logger.debug("Cleaning up similarities older than $retentionDays days")

        // TODO: Implement actual deletion when SimilarityRepository has deleteByDate
        val deletedCount = 0

        logger.debug("Cleaned up $deletedCount old similarities")
        return deletedCount
    }

    /**
     * Manually trigger cleanup with progress callback
     */
    fun performCleanupWithProgress(
        adminUserId: Long = 0,
        onProgress: (Int, String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                onProgress(0, "Starting cleanup...")
                val result = performCleanup(adminUserId)
                onProgress(100, "Cleanup completed: ${result.totalItemsDeleted} items deleted")
            } catch (e: Exception) {
                onProgress(-1, "Cleanup failed: ${e.message}")
                logger.error("Cleanup with progress failed", e)
            }
        }
    }

    /**
     * Preview cleanup - returns what would be deleted without actually deleting
     */
    suspend fun previewCleanup(): CleanupPreview {
        try {
            val settings = adminSettingsRepository.getAdminSettings()
            return CleanupPreview(
                reportRetentionDays = settings.reportRetentionDays,
                submissionRetentionDays = settings.submissionRetentionDays,
                estimatedReportsToDelete = 0, // TODO: Implement
                estimatedSubmissionsToDelete = 0, // TODO: Implement
                estimatedSimilaritiesToDelete = 0 // TODO: Implement
            )
        } catch (e: Exception) {
            logger.error("Preview cleanup failed", e)
            throw e
        }
    }
}

/**
 * Storage statistics data class
 */
data class StorageStatistics(
    val submissionCount: Int,
    val reportCount: Int,
    val similarityCount: Int,
    val estimatedSizeBytes: Long,
    val oldestSubmissionDate: Long?,
    val oldestReportDate: Long?
)

/**
 * Result of cleanup operation
 */
data class CleanupResult(
    val reportsDeleted: Int,
    val submissionsDeleted: Int,
    val similaritiesDeleted: Int
) {
    val totalItemsDeleted: Int
        get() = reportsDeleted + submissionsDeleted + similaritiesDeleted
}

/**
 * Preview of cleanup operation
 */
data class CleanupPreview(
    val reportRetentionDays: Int,
    val submissionRetentionDays: Int,
    val estimatedReportsToDelete: Int,
    val estimatedSubmissionsToDelete: Int,
    val estimatedSimilaritiesToDelete: Int
)
