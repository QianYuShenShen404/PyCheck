package com.example.codechecker.domain.usecase

import com.example.codechecker.domain.repository.AssignmentRepository
import com.example.codechecker.domain.repository.ReportRepository
import com.example.codechecker.domain.repository.SubmissionRepository
import com.example.codechecker.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for exporting data to JSON/CSV
 */
@Singleton
class ExportDataUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val assignmentRepository: AssignmentRepository,
    private val submissionRepository: SubmissionRepository,
    private val reportRepository: ReportRepository,
    private val auditLogger: AuditLogger
) {
    private val json = Json { encodeDefaults = true; ignoreUnknownKeys = true }

    suspend operator fun invoke(
        dataType: DataType,
        format: ExportFormat,
        adminUserId: Long,
        onProgress: ((Int) -> Unit)? = null
    ): Result<String> {
        return try {
            withContext(Dispatchers.IO) {
                onProgress?.invoke(10)

                val data = when (dataType) {
                    DataType.USERS -> exportUsers()
                    DataType.ASSIGNMENTS -> exportAssignments()
                    DataType.SUBMISSIONS -> exportSubmissions()
                    DataType.REPORTS -> exportReports()
                    DataType.ALL -> exportAll(onProgress)
                }

                onProgress?.invoke(90)

                val result = when (format) {
                    ExportFormat.JSON -> data
                    ExportFormat.CSV -> convertToCsv(data, dataType)
                }

                auditLogger.log(
                    adminUserId = adminUserId,
                    action = com.example.codechecker.domain.model.AuditAction.DATA_EXPORT,
                    targetType = com.example.codechecker.domain.model.AuditTargetType.SYSTEM,
                    result = com.example.codechecker.domain.model.AuditResult.SUCCESS,
                    details = "Exported ${dataType.name.lowercase()} as ${format.name}"
                )

                onProgress?.invoke(100)
                Result.success(result)
            }
        } catch (e: Exception) {
            auditLogger.logError(
                adminUserId = adminUserId,
                action = com.example.codechecker.domain.model.AuditAction.DATA_EXPORT,
                targetType = com.example.codechecker.domain.model.AuditTargetType.SYSTEM,
                error = e,
                details = "Failed to export data: ${dataType.name}"
            )
            Result.failure(e)
        }
    }

    suspend fun execute(format: String): Result<String> {
        val exportFormat = when (format.uppercase()) {
            "CSV" -> ExportFormat.CSV
            else -> ExportFormat.JSON
        }
        return invoke(DataType.ALL, exportFormat, adminUserId = 0)
    }

    private suspend fun exportUsers(): String {
        val users = userRepository.getAllUsers()
        return json.encodeToString(users)
    }

    private suspend fun exportAssignments(): String {
        // TODO: Implement when AssignmentRepository has getAllAssignments
        return json.encodeToString(listOf<Any>())
    }

    private suspend fun exportSubmissions(): String {
        // TODO: Implement when SubmissionRepository has getAllSubmissions
        return json.encodeToString(listOf<Any>())
    }

    private suspend fun exportReports(): String {
        // TODO: Implement when ReportRepository has getAllReports
        return json.encodeToString(listOf<Any>())
    }

    private suspend fun exportAll(onProgress: ((Int) -> Unit)?): String {
        onProgress?.invoke(20)
        val users = exportUsers()
        onProgress?.invoke(40)
        val assignments = exportAssignments()
        onProgress?.invoke(60)
        val submissions = exportSubmissions()
        onProgress?.invoke(80)
        val reports = exportReports()

        val exportData = mapOf(
            "users" to users,
            "assignments" to assignments,
            "submissions" to submissions,
            "reports" to reports
        )

        return json.encodeToString(exportData)
    }

    private fun convertToCsv(jsonData: String, dataType: DataType): String {
        // TODO: Implement CSV conversion
        // For now, return JSON as CSV placeholder
        return jsonData
    }
}

enum class DataType {
    USERS, ASSIGNMENTS, SUBMISSIONS, REPORTS, ALL
}

enum class ExportFormat {
    JSON, CSV
}
