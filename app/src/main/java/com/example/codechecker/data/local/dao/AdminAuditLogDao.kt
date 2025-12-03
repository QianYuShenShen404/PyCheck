package com.example.codechecker.data.local.dao

import androidx.room.*
import com.example.codechecker.data.local.entity.AdminAuditLogEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for AdminAuditLog entity
 */
@Dao
interface AdminAuditLogDao {

    /**
     * Insert an audit log entry
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertLog(log: AdminAuditLogEntity): Long

    /**
     * Get all audit logs
     */
    @Query("SELECT * FROM admin_audit_logs ORDER BY timestamp DESC")
    suspend fun getAllLogs(): List<AdminAuditLogEntity>

    /**
     * Get all audit logs as Flow
     */
    @Query("SELECT * FROM admin_audit_logs ORDER BY timestamp DESC")
    fun getAllLogsFlow(): Flow<List<AdminAuditLogEntity>>

    /**
     * Get logs by admin user ID
     */
    @Query("SELECT * FROM admin_audit_logs WHERE adminUserId = :adminUserId ORDER BY timestamp DESC")
    suspend fun getLogsByAdminId(adminUserId: Long): List<AdminAuditLogEntity>

    /**
     * Get logs by action type
     */
    @Query("SELECT * FROM admin_audit_logs WHERE `action` = :action ORDER BY timestamp DESC")
    suspend fun getLogsByAction(action: String): List<AdminAuditLogEntity>

    /**
     * Get logs by target type
     */
    @Query("SELECT * FROM admin_audit_logs WHERE targetType = :targetType ORDER BY timestamp DESC")
    suspend fun getLogsByTargetType(targetType: String): List<AdminAuditLogEntity>

    /**
     * Get logs within time range
     */
    @Query("SELECT * FROM admin_audit_logs WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    suspend fun getLogsByTimeRange(startTime: Long, endTime: Long): List<AdminAuditLogEntity>

    /**
     * Get logs with pagination
     */
    @Query("SELECT * FROM admin_audit_logs ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getLogsPaged(limit: Int, offset: Int): List<AdminAuditLogEntity>

    /**
     * Delete logs older than timestamp
     */
    @Query("DELETE FROM admin_audit_logs WHERE timestamp < :timestamp")
    suspend fun deleteLogsOlderThan(timestamp: Long): Int

    /**
     * Get total log count
     */
    @Query("SELECT COUNT(*) FROM admin_audit_logs")
    suspend fun getLogCount(): Int
}
