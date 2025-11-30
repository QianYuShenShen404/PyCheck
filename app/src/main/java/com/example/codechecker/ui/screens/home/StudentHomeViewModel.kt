package com.example.codechecker.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechecker.data.preference.UserSessionManager
import com.example.codechecker.domain.model.Assignment
import com.example.codechecker.domain.model.User
import com.example.codechecker.domain.usecase.AssignmentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for student home screen
 */
data class StudentHomeUiState(
    val isLoading: Boolean = false,
    val assignments: List<Assignment> = emptyList(),
    val error: String? = null
)

/**
 * ViewModel for student home screen
 */
@HiltViewModel
class StudentHomeViewModel @Inject constructor(
    private val assignmentUseCase: AssignmentUseCase,
    private val userSessionManager: UserSessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentHomeUiState())
    val uiState: StateFlow<StudentHomeUiState> = _uiState.asStateFlow()

    val currentUser: Flow<User?> = userSessionManager.currentUser

    init {
        loadAssignments()
    }

    fun loadAssignments() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val assignments = assignmentUseCase.getAllAssignments()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    assignments = assignments,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    assignments = emptyList(),
                    error = e.message ?: "加载作业失败"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
