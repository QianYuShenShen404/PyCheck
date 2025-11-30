package com.example.codechecker.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechecker.data.preference.UserSessionManager
import com.example.codechecker.domain.model.User
import com.example.codechecker.domain.repository.UserRepository
import com.example.codechecker.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val displayName: String = "",
    val username: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
    val passwordChanging: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userSessionManager: UserSessionManager,
    private val authUseCase: AuthUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val current = userSessionManager.getCurrentUser()
            _uiState.value = ProfileUiState(
                user = current,
                displayName = current?.displayName ?: "",
                username = current?.username ?: ""
            )
        }
    }

    fun updateDisplayName(value: String) {
        _uiState.value = _uiState.value.copy(displayName = value)
    }

    fun saveProfile() {
        viewModelScope.launch {
            val current = _uiState.value.user ?: return@launch
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            try {
                val updated = current.copy(
                    displayName = _uiState.value.displayName,
                    username = _uiState.value.username
                )
                userRepository.updateUser(updated)
                userSessionManager.saveUserSession(updated)
                _uiState.value = _uiState.value.copy(isSaving = false, user = updated)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = e.message ?: "保存失败")
            }
        }
    }

    suspend fun saveProfileWithResult(): Result<Unit> {
        val current = _uiState.value.user ?: return Result.failure(Exception("未登录"))
        _uiState.value = _uiState.value.copy(isSaving = true, error = null)
        return try {
            val updated = current.copy(
                displayName = _uiState.value.displayName,
                username = _uiState.value.username
            )
            userRepository.updateUser(updated)
            userSessionManager.saveUserSession(updated)
            _uiState.value = _uiState.value.copy(isSaving = false, user = updated)
            Result.success(Unit)
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(isSaving = false, error = e.message ?: "保存失败")
            Result.failure(e)
        }
    }

    fun updateUsername(value: String) {
        _uiState.value = _uiState.value.copy(username = value)
    }

    fun changePassword(oldPassword: String, newPassword: String): String? {
        return try {
            val userId = _uiState.value.user?.id ?: return "未登录"
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(passwordChanging = true, error = null)
                val result = authUseCase.changePassword(userId, oldPassword, newPassword)
                result.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(passwordChanging = false)
                    },
                    onFailure = { ex ->
                        _uiState.value = _uiState.value.copy(passwordChanging = false, error = ex.message)
                    }
                )
            }
            null
        } catch (e: Exception) {
            e.message
        }
    }
    suspend fun verifyOldPassword(oldPassword: String): Result<Unit> {
        val userId = _uiState.value.user?.id ?: return Result.failure(Exception("未登录"))
        return authUseCase.verifyPassword(userId, oldPassword)
    }
}
