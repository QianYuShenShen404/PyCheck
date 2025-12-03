package com.example.codechecker.ui.screens.admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechecker.domain.model.AdminAuditLog
import com.example.codechecker.domain.model.Role
import com.example.codechecker.domain.usecase.GetAllUsersUseCase
import com.example.codechecker.domain.usecase.GetAuditLogsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for Admin Dashboard
 */
data class AdminDashboardUiState(
    val totalUsers: Int = 0,
    val activeUsers: Int = 0,
    val adminUsers: Int = 0,
    val recentAuditLogs: List<AdminAuditLog> = emptyList(),
    val isLoading: Boolean = true
)

/**
 * ViewModel for Admin Dashboard
 */
@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val getAuditLogsUseCase: GetAuditLogsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val totalUsers = getAllUsersUseCase.getUserCount()
                val activeUsers = getAllUsersUseCase.getActiveUserCount()
                val adminUsers = getAllUsersUseCase.getUserCountByRole(Role.ADMIN)
                val recentLogs = getAuditLogsUseCase.getRecentLogs(limit = 5)

                _uiState.value = AdminDashboardUiState(
                    totalUsers = totalUsers,
                    activeUsers = activeUsers,
                    adminUsers = adminUsers,
                    recentAuditLogs = recentLogs,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
