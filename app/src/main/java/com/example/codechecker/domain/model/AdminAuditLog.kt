package com.example.codechecker.domain.model

/**
 * Domain model for Admin Audit Log
 */
data class AdminAuditLog(
    val id: Long = 0,
    val adminUserId: Long,
    val action: String,
    val targetType: String,
    val targetId: String?,
    val timestamp: Long,
    val result: String,
    val details: String?
)

/**
 * Audit action types
 */
object AuditAction {
    const val USER_CREATE = "USER_CREATE"
    const val USER_UPDATE = "USER_UPDATE"
    const val USER_DELETE = "USER_DELETE"
    const val USER_DISABLE = "USER_DISABLE"
    const val USER_ENABLE = "USER_ENABLE"
    const val USER_ROLE_CHANGE = "USER_ROLE_CHANGE"
    const val PASSWORD_RESET = "PASSWORD_RESET"
    const val DATA_EXPORT = "DATA_EXPORT"
    const val DATA_IMPORT = "DATA_IMPORT"
    const val DATA_CLEANUP = "DATA_CLEANUP"
    const val SETTINGS_UPDATE = "SETTINGS_UPDATE"
    const val DATABASE_BACKUP = "DATABASE_BACKUP"
    const val DATABASE_RESTORE = "DATABASE_RESTORE"
    const val FORCED_LOGOUT = "FORCED_LOGOUT"
}

/**
 * Audit target types
 */
object AuditTargetType {
    const val USER = "USER"
    const val ASSIGNMENT = "ASSIGNMENT"
    const val SUBMISSION = "SUBMISSION"
    const val REPORT = "REPORT"
    const val SYSTEM = "SYSTEM"
}

/**
 * Audit result types
 */
object AuditResult {
    const val SUCCESS = "SUCCESS"
    const val FAILED = "FAILED"
    const val PARTIAL = "PARTIAL"
}
