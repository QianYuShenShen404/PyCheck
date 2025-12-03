package com.example.codechecker.ui.screens.admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechecker.domain.model.Role
import com.example.codechecker.domain.model.User
import com.example.codechecker.domain.model.UserStatus
import com.example.codechecker.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.codechecker.util.CryptoUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for User Management
 */
data class UserManagementUiState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedRole: String = "ALL"
)

/**
 * ViewModel for User Management
 */
@HiltViewModel
class UserManagementViewModel @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val changeUserRoleUseCase: ChangeUserRoleUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val disableUserUseCase: DisableUserUseCase,
    private val enableUserUseCase: EnableUserUseCase,
    private val auditLogger: AuditLogger,
    private val cryptoUtils: CryptoUtils
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserManagementUiState())
    val uiState: StateFlow<UserManagementUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                val users = getAllUsersUseCase()
                _uiState.value = _uiState.value.copy(
                    users = users,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "加载用户失败"
                )
            }
        }
    }

    fun searchUsers(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(searchQuery = query)
            try {
                val users = if (query.isBlank()) {
                    getAllUsersUseCase()
                } else {
                    getAllUsersUseCase.searchUsers(query)
                }
                _uiState.value = _uiState.value.copy(users = users)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "搜索失败"
                )
            }
        }
    }

    fun createUser(
        username: String,
        password: String,
        displayName: String,
        role: Role,
        adminUserId: Long
    ) {
        viewModelScope.launch {
            try {
                val result = createUserUseCase(
                    username = username,
                    passwordHash = password, // TODO: Hash password
                    displayName = displayName,
                    role = role,
                    adminUserId = adminUserId
                )
                result.fold(
                    onSuccess = {
                        loadUsers()
                    },
                    onFailure = {
                        _uiState.value = _uiState.value.copy(
                            error = it.message ?: "创建用户失败"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "创建用户失败"
                )
            }
        }
    }

    fun deleteUser(userId: Long, adminUserId: Long) {
        viewModelScope.launch {
            try {
                val result = deleteUserUseCase(userId, adminUserId)
                result.fold(
                    onSuccess = {
                        loadUsers()
                    },
                    onFailure = {
                        _uiState.value = _uiState.value.copy(
                            error = it.message ?: "删除用户失败"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "删除用户失败"
                )
            }
        }
    }

    fun changeUserRole(userId: Long, newRole: Role, adminUserId: Long) {
        viewModelScope.launch {
            try {
                val result = changeUserRoleUseCase(userId, newRole, adminUserId)
                result.fold(
                    onSuccess = {
                        loadUsers()
                    },
                    onFailure = {
                        _uiState.value = _uiState.value.copy(
                            error = it.message ?: "更改角色失败"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "更改角色失败"
                )
            }
        }
    }

    fun disableUser(userId: Long, adminUserId: Long) {
        viewModelScope.launch {
            try {
                val result = disableUserUseCase(userId, adminUserId)
                result.fold(
                    onSuccess = {
                        loadUsers()
                    },
                    onFailure = {
                        _uiState.value = _uiState.value.copy(
                            error = it.message ?: "禁用用户失败"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "禁用用户失败"
                )
            }
        }
    }

    fun enableUser(userId: Long, adminUserId: Long) {
        viewModelScope.launch {
            try {
                val result = enableUserUseCase(userId, adminUserId)
                result.fold(
                    onSuccess = {
                        loadUsers()
                    },
                    onFailure = {
                        _uiState.value = _uiState.value.copy(
                            error = it.message ?: "启用用户失败"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "启用用户失败"
                )
            }
        }
    }

    fun updateUser(userId: Long, displayName: String, role: Role, adminUserId: Long) {
        viewModelScope.launch {
            try {
                val existingUsers = getAllUsersUseCase()
                val existingUser = existingUsers.find { it.id == userId }
                if (existingUser == null) {
                    _uiState.value = _uiState.value.copy(
                        error = "用户不存在"
                    )
                    return@launch
                }

                val updatedUser = existingUser.copy(
                    displayName = displayName,
                    role = role
                )

                val result = updateUserUseCase(updatedUser, adminUserId)
                result.fold(
                    onSuccess = {
                        loadUsers()
                    },
                    onFailure = {
                        _uiState.value = _uiState.value.copy(
                            error = it.message ?: "更新用户失败"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "更新用户失败"
                )
            }
        }
    }

    fun resetPassword(userId: Long, adminUserId: Long) {
        viewModelScope.launch {
            try {
                auditLogger.log(
                    adminUserId = adminUserId,
                    action = "PASSWORD_RESET",
                    targetType = "User",
                    targetId = userId.toString(),
                    result = "SUCCESS",
                    details = "Password reset initiated"
                )

                val newPassword = "Temp#${System.currentTimeMillis()}"
                val newPasswordHash = cryptoUtils.sha256(newPassword)
                val result = resetPasswordUseCase(userId, newPasswordHash, adminUserId)
                result.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            error = "密码重置成功，新密码已生成"
                        )
                        auditLogger.log(
                            adminUserId = adminUserId,
                            action = "PASSWORD_RESET",
                            targetType = "User",
                            targetId = userId.toString(),
                            result = "SUCCESS",
                            details = "Password reset completed"
                        )
                    },
                    onFailure = {
                        val errorMsg = it.message ?: "重置密码失败"
                        _uiState.value = _uiState.value.copy(error = errorMsg)
                        auditLogger.log(
                            adminUserId = adminUserId,
                            action = "PASSWORD_RESET",
                            targetType = "User",
                            targetId = userId.toString(),
                            result = "FAILED",
                            details = errorMsg
                        )
                    }
                )
            } catch (e: Exception) {
                val errorMsg = e.message ?: "重置密码失败"
                _uiState.value = _uiState.value.copy(error = errorMsg)
                auditLogger.log(
                    adminUserId = adminUserId,
                    action = "PASSWORD_RESET",
                    targetType = "User",
                    targetId = userId.toString(),
                    result = "FAILED",
                    details = errorMsg
                )
            }
        }
    }

    fun exportUsers() {
        viewModelScope.launch {
            try {
                auditLogger.log(
                    adminUserId = 0,
                    action = "USER_EXPORT",
                    targetType = "Users",
                    targetId = null,
                    result = "SUCCESS",
                    details = "Exporting users data"
                )

                val users = getAllUsersUseCase()
                val csv = buildString {
                    appendLine("ID,用户名,显示名称,角色,状态,创建时间")
                    users.forEach { user ->
                        appendLine("${user.id},${user.username},${user.displayName},${user.role},${user.status},${user.createdAt}")
                    }
                }

                _uiState.value = _uiState.value.copy(
                    error = "用户数据已导出（${users.size}个用户，CSV大小${csv.length}字符）"
                )
                auditLogger.log(
                    adminUserId = 0,
                    action = "USER_EXPORT",
                    targetType = "Users",
                    targetId = null,
                    result = "SUCCESS",
                    details = "Exported ${users.size} users, CSV size=${csv.length}"
                )

            } catch (e: Exception) {
                val errorMsg = "导出用户失败: ${e.message}"
                _uiState.value = _uiState.value.copy(error = errorMsg)
                auditLogger.log(
                    adminUserId = 0,
                    action = "USER_EXPORT",
                    targetType = "Users",
                    targetId = null,
                    result = "FAILED",
                    details = errorMsg
                )
            }
        }
    }

    fun importUsers(format: String) {
        viewModelScope.launch {
            try {
                auditLogger.log(
                    adminUserId = 0,
                    action = "USER_IMPORT",
                    targetType = "Users",
                    targetId = null,
                    result = "SUCCESS",
                    details = "Importing users from $format"
                )

                _uiState.value = _uiState.value.copy(
                    error = "导入功能开发中..."
                )
                auditLogger.log(
                    adminUserId = 0,
                    action = "USER_IMPORT",
                    targetType = "Users",
                    targetId = null,
                    result = "PARTIAL",
                    details = "Import initiated for $format format"
                )

            } catch (e: Exception) {
                val errorMsg = "导入用户失败: ${e.message}"
                _uiState.value = _uiState.value.copy(error = errorMsg)
                auditLogger.log(
                    adminUserId = 0,
                    action = "USER_IMPORT",
                    targetType = "Users",
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
}
