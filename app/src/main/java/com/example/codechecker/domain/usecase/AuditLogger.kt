package com.example.codechecker.domain.usecase

import com.example.codechecker.data.local.dao.AdminAuditLogDao
import com.example.codechecker.data.mapper.AdminAuditLogMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for logging admin actions to audit trail
 */
@Singleton
class AuditLogger @Inject constructor(
    private val adminAuditLogDao: AdminAuditLogDao
) {
    suspend fun log(
        adminUserId: Long,
        action: String,
        targetType: String,
        targetId: String? = null,
        result: String,
        details: String? = null
    ) = withContext(Dispatchers.IO) {
        val auditLog = com.example.codechecker.domain.model.AdminAuditLog(
            adminUserId = adminUserId,
            action = action,
            targetType = targetType,
            targetId = targetId,
            timestamp = System.currentTimeMillis(),
            result = result,
            details = details
        )

        try {
            val entity = AdminAuditLogMapper.toEntity(auditLog)
            adminAuditLogDao.insertLog(entity)
        } catch (e: Exception) {
            // Silent fail to avoid disrupting main operations
            // Could log to different error tracking system if needed
            e.printStackTrace()
        }
    }

    suspend fun logError(
        adminUserId: Long,
        action: String,
        targetType: String,
        error: Exception,
        details: String? = null
    ) {
        log(
            adminUserId = adminUserId,
            action = action,
            targetType = targetType,
            result = com.example.codechecker.domain.model.AuditResult.FAILED,
            details = "${details ?: ""} Error: ${error.message}"
        )
    }

    suspend fun logPartial(
        adminUserId: Long,
        action: String,
        targetType: String,
        targetId: String? = null,
        details: String? = null
    ) {
        log(
            adminUserId = adminUserId,
            action = action,
            targetType = targetType,
            targetId = targetId,
            result = com.example.codechecker.domain.model.AuditResult.PARTIAL,
            details = details
        )
    }
}
