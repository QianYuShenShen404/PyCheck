package com.example.codechecker.ui.screens.admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechecker.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Data Management ViewModel
 * Handles data management operations: backup, restore, export, import, cleanup
 */
data class DataManagementUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: String? = null,
    val storageStats: StorageStats = StorageStats(),
    val isBackupInProgress: Boolean = false,
    val isRestoreInProgress: Boolean = false,
    val isExportInProgress: Boolean = false,
    val isImportInProgress: Boolean = false,
    val isCleanupInProgress: Boolean = false,
    val cleanupPreview: CleanupPreview? = null
)

data class StorageStats(
    val totalUsers: Long = 0,
    val totalAuditLogs: Long = 0,
    val totalSize: Long = 0,
    val availableSpace: Long = 0
)

data class CleanupPreview(
    val reportsToDelete: Long = 0,
    val submissionsToDelete: Long = 0,
    val auditLogsToDelete: Long = 0,
    val estimatedSpaceFreed: Long = 0
)

@HiltViewModel
class DataManagementViewModel @Inject constructor(
    private val databaseBackupUseCase: DatabaseBackupUseCase,
    private val databaseRestoreUseCase: DatabaseRestoreUseCase,
    private val exportDataUseCase: ExportDataUseCase,
    private val importDataUseCase: ImportDataUseCase,
    private val cleanupDataUseCase: DataCleanupUseCase,
    private val storageStatisticsUseCase: StorageStatisticsUseCase,
    private val auditLogger: AuditLogger
) : ViewModel() {

    private val _uiState = MutableStateFlow(DataManagementUiState())
    val uiState: StateFlow<DataManagementUiState> = _uiState.asStateFlow()

    init {
        loadStorageStats()
    }

    private fun loadStorageStats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val stats = storageStatisticsUseCase.execute()
                _uiState.value = _uiState.value.copy(
                    storageStats = StorageStats(
                        totalUsers = stats.getOrDefault("users", 0L),
                        totalAuditLogs = stats.getOrDefault("audit_logs", 0L),
                        totalSize = stats.getOrDefault("total_size", 0L),
                        availableSpace = stats.getOrDefault("available_space", 0L)
                    ),
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "加载存储统计失败: ${e.message}"
                )
            }
        }
    }

    fun backupDatabase() {
        if (_uiState.value.isBackupInProgress) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isBackupInProgress = true,
                error = null,
                success = null
            )
            try {
                auditLogger.log(
                    adminUserId = 0,
                    action = "DATABASE_BACKUP",
                    targetType = "Database",
                    targetId = null,
                    result = "SUCCESS",
                    details = "Starting database backup"
                )

                val result = databaseBackupUseCase.execute()
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isBackupInProgress = false,
                        success = "数据库备份成功完成"
                    )
                    auditLogger.log(
                        adminUserId = 0,
                        action = "DATABASE_BACKUP",
                        targetType = "Database",
                        targetId = null,
                        result = "SUCCESS",
                        details = "Database backup completed"
                    )
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "备份失败"
                    _uiState.value = _uiState.value.copy(
                        isBackupInProgress = false,
                        error = errorMsg
                    )
                    auditLogger.log(
                        adminUserId = 0,
                        action = "DATABASE_BACKUP",
                        targetType = "Database",
                        targetId = null,
                        result = "FAILED",
                        details = errorMsg
                    )
                }
            } catch (e: Exception) {
                val errorMsg = "备份过程中发生错误: ${e.message}"
                _uiState.value = _uiState.value.copy(
                    isBackupInProgress = false,
                    error = errorMsg
                )
                auditLogger.log(
                    adminUserId = 0,
                    action = "DATABASE_BACKUP",
                    targetType = "Database",
                    targetId = null,
                    result = "FAILED",
                    details = errorMsg
                )
            }
        }
    }

    fun restoreDatabase(backupPath: String) {
        if (_uiState.value.isRestoreInProgress) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isRestoreInProgress = true,
                error = null,
                success = null
            )
            try {
                auditLogger.log(
                    adminUserId = 0,
                    action = "DATABASE_RESTORE",
                    targetType = "Database",
                    targetId = null,
                    result = "SUCCESS",
                    details = "Starting database restore from $backupPath"
                )

                val result = databaseRestoreUseCase.execute(backupPath)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isRestoreInProgress = false,
                        success = "数据库恢复成功完成"
                    )
                    auditLogger.log(
                        adminUserId = 0,
                        action = "DATABASE_RESTORE",
                        targetType = "Database",
                        targetId = null,
                        result = "SUCCESS",
                        details = "Database restore completed"
                    )
                    loadStorageStats()
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "恢复失败"
                    _uiState.value = _uiState.value.copy(
                        isRestoreInProgress = false,
                        error = errorMsg
                    )
                    auditLogger.log(
                        adminUserId = 0,
                        action = "DATABASE_RESTORE",
                        targetType = "Database",
                        targetId = null,
                        result = "FAILED",
                        details = errorMsg
                    )
                }
            } catch (e: Exception) {
                val errorMsg = "恢复过程中发生错误: ${e.message}"
                _uiState.value = _uiState.value.copy(
                    isRestoreInProgress = false,
                    error = errorMsg
                )
                auditLogger.log(
                    adminUserId = 0,
                    action = "DATABASE_RESTORE",
                    targetType = "Database",
                    targetId = null,
                    result = "FAILED",
                    details = errorMsg
                )
            }
        }
    }

    fun exportData(format: String) {
        if (_uiState.value.isExportInProgress) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isExportInProgress = true,
                error = null,
                success = null
            )
            try {
                auditLogger.log(
                    adminUserId = 0,
                    action = "DATA_EXPORT",
                    targetType = "Data",
                    targetId = null,
                    result = "SUCCESS",
                    details = "Starting data export in $format format"
                )

                val result = exportDataUseCase.execute(format)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isExportInProgress = false,
                        success = "数据导出成功完成"
                    )
                    auditLogger.log(
                        adminUserId = 0,
                        action = "DATA_EXPORT",
                        targetType = "Data",
                        targetId = null,
                        result = "SUCCESS",
                        details = "Data export completed in $format format"
                    )
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "导出失败"
                    _uiState.value = _uiState.value.copy(
                        isExportInProgress = false,
                        error = errorMsg
                    )
                    auditLogger.log(
                        adminUserId = 0,
                        action = "DATA_EXPORT",
                        targetType = "Data",
                        targetId = null,
                        result = "FAILED",
                        details = errorMsg
                    )
                }
            } catch (e: Exception) {
                val errorMsg = "导出过程中发生错误: ${e.message}"
                _uiState.value = _uiState.value.copy(
                    isExportInProgress = false,
                    error = errorMsg
                )
                auditLogger.log(
                    adminUserId = 0,
                    action = "DATA_EXPORT",
                    targetType = "Data",
                    targetId = null,
                    result = "FAILED",
                    details = errorMsg
                )
            }
        }
    }

    fun importData(format: String) {
        if (_uiState.value.isImportInProgress) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isImportInProgress = true,
                error = null,
                success = null
            )
            try {
                auditLogger.log(
                    adminUserId = 0,
                    action = "DATA_IMPORT",
                    targetType = "Data",
                    targetId = null,
                    result = "SUCCESS",
                    details = "Starting data import from $format format"
                )

                val result = importDataUseCase.execute(format)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isImportInProgress = false,
                        success = "数据导入成功完成"
                    )
                    auditLogger.log(
                        adminUserId = 0,
                        action = "DATA_IMPORT",
                        targetType = "Data",
                        targetId = null,
                        result = "SUCCESS",
                        details = "Data import completed from $format format"
                    )
                    loadStorageStats()
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "导入失败"
                    _uiState.value = _uiState.value.copy(
                        isImportInProgress = false,
                        error = errorMsg
                    )
                    auditLogger.log(
                        adminUserId = 0,
                        action = "DATA_IMPORT",
                        targetType = "Data",
                        targetId = null,
                        result = "FAILED",
                        details = errorMsg
                    )
                }
            } catch (e: Exception) {
                val errorMsg = "导入过程中发生错误: ${e.message}"
                _uiState.value = _uiState.value.copy(
                    isImportInProgress = false,
                    error = errorMsg
                )
                auditLogger.log(
                    adminUserId = 0,
                    action = "DATA_IMPORT",
                    targetType = "Data",
                    targetId = null,
                    result = "FAILED",
                    details = errorMsg
                )
            }
        }
    }

    fun previewCleanup(daysToKeep: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(error = null, success = null)
            try {
                val stats = _uiState.value.storageStats
                val preview = CleanupPreview(
                    reportsToDelete = (stats.totalUsers * 0.1).toLong(),
                    submissionsToDelete = (stats.totalUsers * 0.2).toLong(),
                    auditLogsToDelete = (stats.totalAuditLogs * 0.3).toLong(),
                    estimatedSpaceFreed = stats.totalSize / 4
                )
                _uiState.value = _uiState.value.copy(
                    cleanupPreview = preview
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "预览清理失败: ${e.message}"
                )
            }
        }
    }

    fun cleanupData(
        cleanupLogs: Boolean,
        cleanupOldUsers: Boolean,
        daysToKeep: Int
    ) {
        if (_uiState.value.isCleanupInProgress) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isCleanupInProgress = true,
                error = null,
                success = null
            )
            try {
                val details = buildString {
                    if (cleanupLogs) append("日志, ")
                    if (cleanupOldUsers) append("旧用户, ")
                    append("保留${daysToKeep}天")
                }
                auditLogger.log(
                    adminUserId = 0,
                    action = "DATA_CLEANUP",
                    targetType = "Data",
                    targetId = null,
                    result = "SUCCESS",
                    details = "Starting data cleanup: $details"
                )

                val result = cleanupDataUseCase.execute(
                    cleanupLogs = cleanupLogs,
                    cleanupOldUsers = cleanupOldUsers,
                    daysToKeep = daysToKeep
                )
                if (result.isSuccess) {
                    val cleanedItems = result.getOrDefault(0)
                    _uiState.value = _uiState.value.copy(
                        isCleanupInProgress = false,
                        success = "数据清理完成，清理了 $cleanedItems 项记录",
                        cleanupPreview = null
                    )
                    auditLogger.log(
                        adminUserId = 0,
                        action = "DATA_CLEANUP",
                        targetType = "Data",
                        targetId = null,
                        result = "SUCCESS",
                        details = "Data cleanup completed, removed $cleanedItems items"
                    )
                    loadStorageStats()
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "清理失败"
                    _uiState.value = _uiState.value.copy(
                        isCleanupInProgress = false,
                        error = errorMsg
                    )
                    auditLogger.log(
                        adminUserId = 0,
                        action = "DATA_CLEANUP",
                        targetType = "Data",
                        targetId = null,
                        result = "FAILED",
                        details = errorMsg
                    )
                }
            } catch (e: Exception) {
                val errorMsg = "清理过程中发生错误: ${e.message}"
                _uiState.value = _uiState.value.copy(
                    isCleanupInProgress = false,
                    error = errorMsg
                )
                auditLogger.log(
                    adminUserId = 0,
                    action = "DATA_CLEANUP",
                    targetType = "Data",
                    targetId = null,
                    result = "FAILED",
                    details = errorMsg
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(success = null)
    }

    fun clearPreview() {
        _uiState.value = _uiState.value.copy(cleanupPreview = null)
    }

    fun refreshStats() {
        loadStorageStats()
    }
}
