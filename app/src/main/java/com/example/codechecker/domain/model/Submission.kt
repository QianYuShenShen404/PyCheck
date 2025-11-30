package com.example.codechecker.domain.model

/**
 * Domain model for Submission
 */
data class Submission(
    val id: Long = 0,
    val studentId: Long,
    val assignmentId: Long,
    val fileName: String,
    val codeContent: String,
    val codeHash: String,
    val status: SubmissionStatus,
    val submittedAt: Long,
    val studentName: String = "",
    val fileSize: Long = 0
)

/**
 * Submission status enum
 */
enum class SubmissionStatus(val value: String) {
    SUBMITTED("SUBMITTED"),
    ANALYZED("ANALYZED"),
    PROCESSED("PROCESSED");

    companion object {
        fun fromValue(value: String): SubmissionStatus {
            return values().find { it.value == value } ?: SUBMITTED
        }
    }
}
