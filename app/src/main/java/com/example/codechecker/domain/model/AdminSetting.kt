package com.example.codechecker.domain.model

/**
 * Domain model for Admin Settings
 */
data class AdminSettings(
    val similarityThreshold: Int = 60,
    val reportRetentionDays: Int = 30,
    val submissionRetentionDays: Int = 180,
    val fastCompareMode: Boolean = false,
    val logLevel: LogLevel = LogLevel.INFO,
    val autoCleanupEnabled: Boolean = false,
    val maxSubmissionsPerAssignment: Int = 200
)

/**
 * Log level enum
 */
enum class LogLevel(val value: String) {
    DEBUG("DEBUG"),
    INFO("INFO"),
    WARN("WARN"),
    ERROR("ERROR");

    companion object {
        fun fromValue(value: String): LogLevel {
            val normalized = value.uppercase()
            return values().find { it.value == normalized } ?: INFO
        }
    }
}

/**
 * Individual admin setting entry
 */
data class AdminSetting(
    val key: String,
    val value: String,
    val type: SettingType,
    val updatedAt: Long,
    val updatedBy: Long
)

/**
 * Setting type enum
 */
enum class SettingType(val value: String) {
    INT("INT"),
    STRING("STRING"),
    BOOLEAN("BOOLEAN"),
    JSON("JSON");

    companion object {
        fun fromValue(value: String): SettingType {
            val normalized = value.uppercase()
            return values().find { it.value == normalized } ?: STRING
        }
    }
}
