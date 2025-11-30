package com.example.codechecker.domain.model

/**
 * Domain model for Report
 */
data class Report(
    val id: Long = 0,
    val assignmentId: Long,
    val executorId: Long,
    val status: ReportStatus,
    val totalSubmissions: Int,
    val totalPairs: Int,
    val createdAt: Long,
    val completedAt: Long?
)

/**
 * Report status enum
 */
enum class ReportStatus(val value: String) {
    PENDING("PENDING"),
    COMPLETED("COMPLETED"),
    FAILED("FAILED");

    companion object {
        fun fromValue(value: String): ReportStatus {
            return values().find { it.value == value } ?: PENDING
        }
    }
}
