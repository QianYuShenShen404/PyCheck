package com.example.codechecker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Admin settings entity for storing system configuration
 */
@Entity(tableName = "admin_settings")
data class AdminSettingEntity(
    @PrimaryKey
    val key: String,

    val value: String,

    val type: String, // "INT", "STRING", "BOOLEAN", "JSON"

    val updatedAt: Long,

    val updatedBy: Long
)
