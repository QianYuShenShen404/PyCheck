package com.example.codechecker.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Report entity representing a plagiarism check report
 */
@Entity(tableName = "plagiarism_reports")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "assignment_id")
    val assignmentId: Long,

    @ColumnInfo(name = "executor_id")
    val executorId: Long,

    @ColumnInfo(name = "status")
    val status: String, // "PENDING", "COMPLETED", "FAILED"

    @ColumnInfo(name = "total_submissions")
    val totalSubmissions: Int,

    @ColumnInfo(name = "total_pairs")
    val totalPairs: Int,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "completed_at")
    val completedAt: Long?
)
