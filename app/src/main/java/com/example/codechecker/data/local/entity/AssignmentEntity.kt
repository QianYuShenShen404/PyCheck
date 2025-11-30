package com.example.codechecker.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Assignment entity representing a homework/assignment
 */
@Entity(tableName = "assignments")
data class AssignmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "teacher_id")
    val teacherId: Long,

    @ColumnInfo(name = "due_date")
    val dueDate: Long?,

    @ColumnInfo(name = "submission_limit")
    val submissionLimit: Int, // 200 for small, 500 for large, 0 for unlimited

    @ColumnInfo(name = "python_version")
    val pythonVersion: String, // "PYTHON2", "PYTHON3", "COMPATIBLE"

    @ColumnInfo(name = "status")
    val status: String, // "DRAFT", "ACTIVE", "CLOSED"

    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
