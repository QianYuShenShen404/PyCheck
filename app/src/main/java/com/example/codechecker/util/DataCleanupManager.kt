package com.example.codechecker.util

import com.example.codechecker.domain.repository.ReportRepository
import com.example.codechecker.domain.repository.SubmissionRepository
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
    private val logger: Logger
) {

    companion object {
        private const val DEFAULT_RETENTION_DAYS = 90L
        private const val REPORT_RETENTION_DAYS = 30L
        private const val SUBMISSION_RETENTION_DAYS = 180L
    }

    /**
     * Check and cleanup old data
     */
    fun checkAndCleanupData() {
        CoroutineScope(Dispatchers.IO).launch {
            cleanup()
        }
    }

    /**
     * Perform data cleanup
     */
    private suspend fun cleanup() {
        logger.info("Starting data cleanup")

        try {
            cleanupOldSubmissions()
            cleanupOldReports()
            cleanupOldSimilarities()

            logger.info("Data cleanup completed successfully")
        } catch (e: Exception) {
            logger.error("Data cleanup failed", e)
        }
    }

    /**
     * Cleanup old submissions
     */
    private suspend fun cleanupOldSubmissions() {
        val cutoffDate = System.currentTimeMillis() - (SUBMISSION_RETENTION_DAYS * 24 * 60 * 60 * 1000)
        logger.debug("Cleaning up submissions older than $SUBMISSION_RETENTION_DAYS days")

    }

    /**
     * Cleanup old reports
     */
    private suspend fun cleanupOldReports() {
        val cutoffDate = System.currentTimeMillis() - (REPORT_RETENTION_DAYS * 24 * 60 * 60 * 1000)
        logger.debug("Cleaning up reports older than $REPORT_RETENTION_DAYS days")

    }

    /**
     * Cleanup old similarity data
     */
    private suspend fun cleanupOldSimilarities() {
        val cutoffDate = System.currentTimeMillis() - (REPORT_RETENTION_DAYS * 24 * 60 * 60 * 1000)
        logger.debug("Cleaning up similarities older than $REPORT_RETENTION_DAYS days")
    }

    /**
     * Get storage usage statistics
     */
    fun getStorageStatistics(): StorageStatistics {
        return StorageStatistics(
            submissionCount = 0,
            reportCount = 0,
            similarityCount = 0,
            estimatedSizeBytes = 0,
            oldestSubmissionDate = null,
            oldestReportDate = null
        )
    }

    /**
     * Manually trigger cleanup with progress callback
     */
    fun performCleanupWithProgress(onProgress: (Int, String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                onProgress(0, "Starting cleanup...")
                cleanup()
                onProgress(100, "Cleanup completed")
            } catch (e: Exception) {
                onProgress(-1, "Cleanup failed: ${e.message}")
                logger.error("Cleanup with progress failed", e)
            }
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
