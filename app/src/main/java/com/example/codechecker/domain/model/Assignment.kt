package com.example.codechecker.domain.model

/**
 * Domain model for Assignment
 */
data class Assignment(
    val id: Long = 0,
    val title: String,
    val description: String,
    val teacherId: Long,
    val dueDate: Long?,
    val submissionLimit: SubmissionLimit,
    val pythonVersion: PythonVersion,
    val status: AssignmentStatus,
    val createdAt: Long
)

/**
 * Submission limit enum
 */
enum class SubmissionLimit(val value: Int) {
    SMALL(200),
    LARGE(500),
    UNLIMITED(0);

    companion object {
        fun fromValue(value: Int): SubmissionLimit {
            return values().find { it.value == value } ?: SMALL
        }
    }
}

/**
 * Python version enum
 */
enum class PythonVersion(val value: String) {
    PYTHON2("PYTHON2"),
    PYTHON3("PYTHON3"),
    COMPATIBLE("COMPATIBLE");

    companion object {
        fun fromValue(value: String): PythonVersion {
            return values().find { it.value == value } ?: COMPATIBLE
        }
    }
}

/**
 * Assignment status enum
 */
enum class AssignmentStatus(val value: String) {
    DRAFT("DRAFT"),
    ACTIVE("ACTIVE"),
    CLOSED("CLOSED");

    companion object {
        fun fromValue(value: String): AssignmentStatus {
            return values().find { it.value == value } ?: DRAFT
        }
    }
}
