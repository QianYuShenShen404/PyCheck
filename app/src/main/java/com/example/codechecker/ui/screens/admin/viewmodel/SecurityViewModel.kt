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
 * Security ViewModel
 * Handles security monitoring, risk scanning, session management, and security alerts
 */
data class SecurityUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: String? = null,
    val securityAlerts: List<SecurityAlert> = emptyList(),
    val activeSessions: List<SecuritySession> = emptyList(),
    val securityStats: SecurityStats = SecurityStats(),
    val isScanning: Boolean = false,
    val lastScanTime: Long? = null
)

data class SecurityAlert(
    val id: String,
    val type: String,
    val severity: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean
)

data class SecuritySession(
    val id: String,
    val userId: String,
    val userEmail: String,
    val deviceInfo: String,
    val ipAddress: String,
    val startTime: Long,
    val isActive: Boolean
)

data class SecurityStats(
    val totalAlerts: Int = 0,
    val unreadAlerts: Int = 0,
    val activeSessions: Int = 0,
    val failedLoginAttempts: Int = 0,
    val suspiciousActivity: Int = 0,
    val lastRiskScan: Long? = null
)

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val getSecurityAlertsUseCase: GetSecurityAlertsUseCase,
    private val getActiveSessionsUseCase: GetActiveSessionsUseCase,
    private val terminateSessionUseCase: TerminateSessionUseCase,
    private val performRiskScanUseCase: PerformRiskScanUseCase,
    private val markAlertAsReadUseCase: MarkAlertAsReadUseCase,
    private val clearSecurityAlertUseCase: ClearSecurityAlertUseCase,
    private val auditLogger: AuditLogger
) : ViewModel() {

    private val _uiState = MutableStateFlow(SecurityUiState())
    val uiState: StateFlow<SecurityUiState> = _uiState.asStateFlow()

    init {
        loadSecurityData()
    }

    private fun loadSecurityData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                loadSecurityAlerts()
                loadActiveSessions()
                loadSecurityStats()
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "加载安全数据失败: ${e.message}"
                )
            }
        }
    }

    private suspend fun loadSecurityAlerts() {
        try {
            val alerts = getSecurityAlertsUseCase.execute()
            _uiState.value = _uiState.value.copy(securityAlerts = alerts)
        } catch (e: Exception) {
            // Silent fail for alerts loading
        }
    }

    private suspend fun loadActiveSessions() {
        try {
            val sessions = getActiveSessionsUseCase.execute()
            _uiState.value = _uiState.value.copy(activeSessions = sessions)
        } catch (e: Exception) {
            // Silent fail for sessions loading
        }
    }

    private suspend fun loadSecurityStats() {
        try {
            val stats = SecurityStats(
                totalAlerts = _uiState.value.securityAlerts.size,
                unreadAlerts = _uiState.value.securityAlerts.count { !it.isRead },
                activeSessions = _uiState.value.activeSessions.count { it.isActive },
                failedLoginAttempts = _uiState.value.securityAlerts.count { it.type == "FAILED_LOGIN" },
                suspiciousActivity = _uiState.value.securityAlerts.count { it.severity == "HIGH" },
                lastRiskScan = _uiState.value.lastScanTime
            )
            _uiState.value = _uiState.value.copy(securityStats = stats)
        } catch (e: Exception) {
            // Silent fail for stats loading
        }
    }

    fun performRiskScan() {
        if (_uiState.value.isScanning) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isScanning = true,
                error = null
            )
            try {
                auditLogger.log(
                    adminUserId = 0,
                    action = "SECURITY_SCAN",
                    targetType = "Security",
                    targetId = null,
                    result = "SUCCESS",
                    details = "Starting security risk scan"
                )

                val result = performRiskScanUseCase.execute()
                if (result.isSuccess) {
                    val scanTime = System.currentTimeMillis()
                    _uiState.value = _uiState.value.copy(
                        isScanning = false,
                        lastScanTime = scanTime,
                        success = "安全风险扫描完成"
                    )
                    auditLogger.log(
                        adminUserId = 0,
                        action = "SECURITY_SCAN",
                        targetType = "Security",
                        targetId = null,
                        result = "SUCCESS",
                        details = "Security risk scan completed"
                    )
                    loadSecurityData()
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "扫描失败"
                    _uiState.value = _uiState.value.copy(
                        isScanning = false,
                        error = errorMsg
                    )
                    auditLogger.log(
                        adminUserId = 0,
                        action = "SECURITY_SCAN",
                        targetType = "Security",
                        targetId = null,
                        result = "FAILED",
                        details = errorMsg
                    )
                }
            } catch (e: Exception) {
                val errorMsg = "扫描过程中发生错误: ${e.message}"
                _uiState.value = _uiState.value.copy(
                    isScanning = false,
                    error = errorMsg
                )
                auditLogger.log(
                    adminUserId = 0,
                    action = "SECURITY_SCAN",
                    targetType = "Security",
                    targetId = null,
                    result = "FAILED",
                    details = errorMsg
                )
            }
        }
    }

    fun terminateSession(sessionId: String) {
        viewModelScope.launch {
            try {
                auditLogger.log(
                    adminUserId = 0,
                    action = "SESSION_TERMINATE",
                    targetType = "Security",
                    targetId = sessionId,
                    result = "SUCCESS",
                    details = "Terminating session"
                )

                val result = terminateSessionUseCase.execute(sessionId)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        success = "会话已终止"
                    )
                    auditLogger.log(
                        adminUserId = 0,
                        action = "SESSION_TERMINATE",
                        targetType = "Security",
                        targetId = sessionId,
                        result = "SUCCESS",
                        details = "Session terminated"
                    )
                    loadActiveSessions()
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "终止会话失败"
                    _uiState.value = _uiState.value.copy(error = errorMsg)
                    auditLogger.log(
                        adminUserId = 0,
                        action = "SESSION_TERMINATE",
                        targetType = "Security",
                        targetId = sessionId,
                        result = "FAILED",
                        details = errorMsg
                    )
                }
            } catch (e: Exception) {
                val errorMsg = "终止会话时发生错误: ${e.message}"
                _uiState.value = _uiState.value.copy(error = errorMsg)
                auditLogger.log(
                    adminUserId = 0,
                    action = "SESSION_TERMINATE",
                    targetType = "Security",
                    targetId = sessionId,
                    result = "FAILED",
                    details = errorMsg
                )
            }
        }
    }

    fun markAlertAsRead(alertId: String) {
        viewModelScope.launch {
            try {
                val result = markAlertAsReadUseCase.execute(alertId)
                if (result.isSuccess) {
                    loadSecurityAlerts()
                    loadSecurityStats()
                }
            } catch (e: Exception) {
                // Silent fail for mark as read
            }
        }
    }

    fun clearAlert(alertId: String) {
        viewModelScope.launch {
            try {
                auditLogger.log(
                    adminUserId = 0,
                    action = "ALERT_CLEAR",
                    targetType = "Security",
                    targetId = alertId,
                    result = "SUCCESS",
                    details = "Clearing security alert"
                )

                val result = clearSecurityAlertUseCase.execute(alertId)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        success = "安全警告已清除"
                    )
                    auditLogger.log(
                        adminUserId = 0,
                        action = "ALERT_CLEAR",
                        targetType = "Security",
                        targetId = alertId,
                        result = "SUCCESS",
                        details = "Security alert cleared"
                    )
                    loadSecurityData()
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "清除警告失败"
                    _uiState.value = _uiState.value.copy(error = errorMsg)
                    auditLogger.log(
                        adminUserId = 0,
                        action = "ALERT_CLEAR",
                        targetType = "Security",
                        targetId = alertId,
                        result = "FAILED",
                        details = errorMsg
                    )
                }
            } catch (e: Exception) {
                val errorMsg = "清除警告时发生错误: ${e.message}"
                _uiState.value = _uiState.value.copy(error = errorMsg)
                auditLogger.log(
                    adminUserId = 0,
                    action = "ALERT_CLEAR",
                    targetType = "Security",
                    targetId = alertId,
                    result = "FAILED",
                    details = errorMsg
                )
            }
        }
    }

    fun refreshSecurityData() {
        loadSecurityData()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(success = null)
    }

    fun terminateAllSessions() {
        viewModelScope.launch {
            try {
                auditLogger.log(
                    adminUserId = 0,
                    action = "BULK_SESSION_TERMINATE",
                    targetType = "Security",
                    targetId = null,
                    result = "SUCCESS",
                    details = "Starting bulk session termination"
                )

                val activeSessions = _uiState.value.activeSessions.filter { it.isActive }
                var successCount = 0
                var failCount = 0

                activeSessions.forEach { session ->
                    try {
                        val result = terminateSessionUseCase.execute(session.id)
                        if (result.isSuccess) {
                            successCount++
                        } else {
                            failCount++
                        }
                    } catch (e: Exception) {
                        failCount++
                    }
                }

                val message = "批量终止会话完成: 成功 $successCount, 失败 $failCount"
                _uiState.value = _uiState.value.copy(
                    success = message
                )
                auditLogger.log(
                    adminUserId = 0,
                    action = "BULK_SESSION_TERMINATE",
                    targetType = "Security",
                    targetId = null,
                    result = "SUCCESS",
                    details = message
                )
                loadActiveSessions()
            } catch (e: Exception) {
                val errorMsg = "批量终止会话时发生错误: ${e.message}"
                _uiState.value = _uiState.value.copy(error = errorMsg)
                auditLogger.log(
                    adminUserId = 0,
                    action = "BULK_SESSION_TERMINATE",
                    targetType = "Security",
                    targetId = null,
                    result = "FAILED",
                    details = errorMsg
                )
            }
        }
    }

    fun getAlertSeverityColor(severity: String): String {
        return when (severity) {
            "LOW" -> "#4CAF50"
            "MEDIUM" -> "#FF9800"
            "HIGH" -> "#F44336"
            "CRITICAL" -> "#9C27B0"
            else -> "#757575"
        }
    }
}
