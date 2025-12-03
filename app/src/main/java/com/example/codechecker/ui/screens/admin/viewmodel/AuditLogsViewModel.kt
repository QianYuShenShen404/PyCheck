package com.example.codechecker.ui.screens.admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechecker.domain.model.AdminAuditLog
import com.example.codechecker.domain.usecase.GetAuditLogsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * UI state for Audit Logs
 */
data class AuditLogsUiState(
    val logs: List<AdminAuditLog> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedAction: String = "ALL",
    val selectedResult: String = "ALL",
    val startDate: Long? = null,
    val endDate: Long? = null,
    val currentPage: Int = 0,
    val pageSize: Int = 50,
    val totalPages: Int = 0,
    val hasMore: Boolean = false
)

/**
 * ViewModel for Audit Logs
 */
@HiltViewModel
class AuditLogsViewModel @Inject constructor(
    private val getAuditLogsUseCase: GetAuditLogsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuditLogsUiState())
    val uiState: StateFlow<AuditLogsUiState> = _uiState.asStateFlow()

    init {
        loadLogs()
    }

    fun loadLogs() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                val logs = getAuditLogsUseCase()
                _uiState.value = _uiState.value.copy(
                    logs = logs,
                    isLoading = false,
                    totalPages = (logs.size + _uiState.value.pageSize - 1) / _uiState.value.pageSize
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "加载审计日志失败"
                )
            }
        }
    }

    fun searchLogs(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(searchQuery = query)
            try {
                val logs = if (query.isBlank()) {
                    getAuditLogsUseCase()
                } else {
                    getAuditLogsUseCase().filter { log ->
                        log.action.contains(query, ignoreCase = true) ||
                                log.details?.contains(query, ignoreCase = true) == true
                    }
                }
                _uiState.value = _uiState.value.copy(logs = logs)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "搜索失败"
                )
            }
        }
    }

    fun filterByAction(action: String) {
        _uiState.value = _uiState.value.copy(selectedAction = action)
        applyFilters()
    }

    fun filterByResult(result: String) {
        _uiState.value = _uiState.value.copy(selectedResult = result)
        applyFilters()
    }

    fun setDateRange(startDate: Long?, endDate: Long?) {
        _uiState.value = _uiState.value.copy(
            startDate = startDate,
            endDate = endDate
        )
        applyFilters()
    }

    private fun applyFilters() {
        viewModelScope.launch {
            try {
                var logs = getAuditLogsUseCase()

                // Apply search query filter
                val query = _uiState.value.searchQuery
                if (query.isNotBlank()) {
                    logs = logs.filter { log ->
                        log.action.contains(query, ignoreCase = true) ||
                                log.details?.contains(query, ignoreCase = true) == true
                    }
                }

                // Apply action filter
                val action = _uiState.value.selectedAction
                if (action != "ALL") {
                    logs = logs.filter { it.action == action }
                }

                // Apply result filter
                val result = _uiState.value.selectedResult
                if (result != "ALL") {
                    logs = logs.filter { it.result == result }
                }

                // Apply date range filter
                _uiState.value.startDate?.let { start ->
                    logs = logs.filter { it.timestamp >= start }
                }
                _uiState.value.endDate?.let { end ->
                    logs = logs.filter { it.timestamp <= end }
                }

                _uiState.value = _uiState.value.copy(
                    logs = logs,
                    totalPages = (logs.size + _uiState.value.pageSize - 1) / _uiState.value.pageSize
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "筛选失败"
                )
            }
        }
    }

    fun exportLogs(): String {
        val logs = _uiState.value.logs
        val sb = StringBuilder()
        sb.appendLine("操作时间,管理员ID,操作类型,目标类型,目标ID,结果,详情")
        logs.forEach { log ->
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(Date(log.timestamp))
            sb.appendLine(
                "${date},${log.adminUserId},${log.action},${log.targetType},${log.targetId ?: ""},${log.result},${log.details ?: ""}"
            )
        }
        return sb.toString()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun getAvailableActions(): List<String> {
        return listOf(
            "ALL",
            "USER_CREATE",
            "USER_UPDATE",
            "USER_DELETE",
            "USER_DISABLE",
            "USER_ENABLE",
            "USER_ROLE_CHANGE",
            "PASSWORD_RESET",
            "DATA_EXPORT",
            "DATA_IMPORT",
            "DATA_CLEANUP",
            "SETTINGS_UPDATE",
            "DATABASE_BACKUP",
            "DATABASE_RESTORE"
        )
    }

    fun getAvailableResults(): List<String> {
        return listOf("ALL", "SUCCESS", "FAILED", "PARTIAL")
    }

    fun getActionDisplayName(action: String): String {
        return when (action) {
            "ALL" -> "全部"
            "USER_CREATE" -> "创建用户"
            "USER_UPDATE" -> "更新用户"
            "USER_DELETE" -> "删除用户"
            "USER_DISABLE" -> "禁用用户"
            "USER_ENABLE" -> "启用用户"
            "USER_ROLE_CHANGE" -> "更改角色"
            "PASSWORD_RESET" -> "重置密码"
            "DATA_EXPORT" -> "导出数据"
            "DATA_IMPORT" -> "导入数据"
            "DATA_CLEANUP" -> "清理数据"
            "SETTINGS_UPDATE" -> "更新设置"
            "DATABASE_BACKUP" -> "数据库备份"
            "DATABASE_RESTORE" -> "数据库恢复"
            else -> action
        }
    }

    fun getResultDisplayName(result: String): String {
        return when (result) {
            "ALL" -> "全部"
            "SUCCESS" -> "成功"
            "FAILED" -> "失败"
            "PARTIAL" -> "部分成功"
            else -> result
        }
    }
}
