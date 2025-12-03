package com.example.codechecker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Admin audit log entity for tracking administrative actions
 */
@Entity(tableName = "admin_audit_logs")
data class AdminAuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val adminUserId: Long,

    val action: String,

    val targetType: String,

    val targetId: String?,

    val timestamp: Long,

    val result: String,

    val details: String?
)
