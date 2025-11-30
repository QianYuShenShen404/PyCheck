package com.example.codechecker.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechecker.data.preference.UserSessionManager
import com.example.codechecker.domain.usecase.AuthUseCase
import com.example.codechecker.util.CryptoUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for registration screen
 */
data class RegisterUiState(
    val isLoading: Boolean = false,
    val isRegistrationSuccess: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for registration screen
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val userSessionManager: UserSessionManager,
    private val cryptoUtils: CryptoUtils
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(
        username: String,
        password: String,
        displayName: String,
        role: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val result = authUseCase.registerUser(
                    username = username,
                    password = password,
                    displayName = displayName,
                    role = role,
                    passwordHash = { input -> cryptoUtils.sha256(input) }
                )

                result.fold(
                    onSuccess = { user ->
                        userSessionManager.saveUserSession(user)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRegistrationSuccess = true,
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRegistrationSuccess = false,
                            error = exception.message ?: "注册失败"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRegistrationSuccess = false,
                    error = e.message ?: "注册失败，请重试"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
