package com.example.codechecker.domain.usecase

import com.example.codechecker.data.local.dao.AdminAuditLogDao
import com.example.codechecker.data.mapper.AdminAuditLogMapper
import com.example.codechecker.domain.model.AdminAuditLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for getting audit logs with filtering
 */
@Singleton
class GetAuditLogsUseCase @Inject constructor(
    private val adminAuditLogDao: AdminAuditLogDao
) {
    suspend operator fun invoke(): List<AdminAuditLog> {
        val entities = adminAuditLogDao.getAllLogs()
        return AdminAuditLogMapper.toDomainList(entities)
    }

    fun getAllLogsFlow(): Flow<List<AdminAuditLog>> {
        return adminAuditLogDao.getAllLogsFlow().map { entities ->
            AdminAuditLogMapper.toDomainList(entities)
        }
    }

    suspend fun getLogsByAdminId(adminUserId: Long): List<AdminAuditLog> {
        val entities = adminAuditLogDao.getLogsByAdminId(adminUserId)
        return AdminAuditLogMapper.toDomainList(entities)
    }

    suspend fun getLogsByAction(action: String): List<AdminAuditLog> {
        val entities = adminAuditLogDao.getLogsByAction(action)
        return AdminAuditLogMapper.toDomainList(entities)
    }

    suspend fun getLogsByTargetType(targetType: String): List<AdminAuditLog> {
        val entities = adminAuditLogDao.getLogsByTargetType(targetType)
        return AdminAuditLogMapper.toDomainList(entities)
    }

    suspend fun getLogsByTimeRange(startTime: Long, endTime: Long): List<AdminAuditLog> {
        val entities = adminAuditLogDao.getLogsByTimeRange(startTime, endTime)
        return AdminAuditLogMapper.toDomainList(entities)
    }

    suspend fun getLogsPaged(limit: Int, offset: Int): List<AdminAuditLog> {
        val entities = adminAuditLogDao.getLogsPaged(limit, offset)
        return AdminAuditLogMapper.toDomainList(entities)
    }

    suspend fun getLogCount(): Int {
        return adminAuditLogDao.getLogCount()
    }

    suspend fun deleteLogsOlderThan(timestamp: Long): Int {
        return adminAuditLogDao.deleteLogsOlderThan(timestamp)
    }

    suspend fun getRecentLogs(limit: Int = 5): List<AdminAuditLog> {
        return getLogsPaged(limit, 0)
    }
}
