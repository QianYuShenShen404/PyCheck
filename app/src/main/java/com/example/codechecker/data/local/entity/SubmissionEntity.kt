package com.example.codechecker.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Submission entity representing a code submission
 */
@Entity(tableName = "submissions")
data class SubmissionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "student_id")
    val studentId: Long,

    @ColumnInfo(name = "assignment_id")
    val assignmentId: Long,

    @ColumnInfo(name = "file_name")
    val fileName: String,

    @ColumnInfo(name = "code_content")
    val codeContent: String,

    @ColumnInfo(name = "code_hash")
    val codeHash: String,

    @ColumnInfo(name = "status")
    val status: String, // "SUBMITTED", "ANALYZED", "PROCESSED"

    @ColumnInfo(name = "submitted_at")
    val submittedAt: Long,

    @ColumnInfo(name = "student_number")
    val studentNumber: String = "",

    @ColumnInfo(name = "student_name")
    val studentName: String = ""
)
