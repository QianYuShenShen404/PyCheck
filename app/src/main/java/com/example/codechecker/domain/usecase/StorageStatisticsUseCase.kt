package com.example.codechecker.domain.usecase

import android.content.Context
import com.example.codechecker.data.local.database.AppDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import com.example.codechecker.domain.repository.AssignmentRepository
import com.example.codechecker.domain.repository.ReportRepository
import com.example.codechecker.domain.repository.SubmissionRepository
import com.example.codechecker.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for getting storage statistics
 */
@Singleton
class StorageStatisticsUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val userRepository: UserRepository,
    private val assignmentRepository: AssignmentRepository,
    private val submissionRepository: SubmissionRepository,
    private val reportRepository: ReportRepository
) {
    suspend fun execute(): Map<String, Long> {
        val stats = computeStatistics()
        return mapOf(
            "users" to stats.userCount.toLong(),
            "audit_logs" to 0L,
            "total_size" to stats.totalFileSizeBytes,
            "available_space" to stats.freeSpaceBytes
        )
    }
    suspend operator fun invoke(): Result<StorageStatistics> {
        return try {
            withContext(Dispatchers.IO) {
                val stats = computeStatistics()
                Result.success(stats)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun computeStatistics(): StorageStatistics {
        // Get database file size
        val dbFile = context.getDatabasePath("codechecker_database")
        val dbSize = if (dbFile.exists()) dbFile.length() else 0L

        // Get table statistics
        val userCount = userRepository.getUserCount()
        val activeUserCount = userRepository.getActiveUserCount()

        // TODO: Get counts from other repositories when implemented
        val assignmentCount = 0
        val submissionCount = 0
        val reportCount = 0
        val similarityCount = 0

        // Get total file size of all files in app directory
        val appDir = context.getFilesDir()
        val totalSize = getDirectorySize(appDir)

        return StorageStatistics(
            userCount = userCount,
            activeUserCount = activeUserCount,
            assignmentCount = assignmentCount,
            submissionCount = submissionCount,
            reportCount = reportCount,
            similarityCount = similarityCount,
            databaseSizeBytes = dbSize,
            totalFileSizeBytes = totalSize,
            freeSpaceBytes = getFreeSpaceBytes(),
            oldestSubmissionDate = null, // TODO: Implement
            oldestReportDate = null // TODO: Implement
        )
    }

    private fun getDirectorySize(dir: File): Long {
        var size = 0L
        if (dir.exists()) {
            val files = dir.listFiles()
            if (files != null) {
                for (file in files) {
                    size += if (file.isDirectory) {
                        getDirectorySize(file)
                    } else {
                        file.length()
                    }
                }
            }
        }
        return size
    }

    private fun getFreeSpaceBytes(): Long {
        val stats = android.os.StatFs(context.filesDir.absolutePath)
        return stats.availableBytes
    }
}

data class StorageStatistics(
    val userCount: Int,
    val activeUserCount: Int,
    val assignmentCount: Int,
    val submissionCount: Int,
    val reportCount: Int,
    val similarityCount: Int,
    val databaseSizeBytes: Long,
    val totalFileSizeBytes: Long,
    val freeSpaceBytes: Long,
    val oldestSubmissionDate: Long?,
    val oldestReportDate: Long?
)
