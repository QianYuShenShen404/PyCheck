package com.example.codechecker.ui.screens.assignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechecker.data.preference.UserSessionManager
import com.example.codechecker.domain.model.Assignment
import com.example.codechecker.domain.usecase.AssignmentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for assignment list screen
 */
data class AssignmentListUiState(
    val assignments: List<Assignment> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for assignment list screen
 */
@HiltViewModel
class AssignmentListViewModel @Inject constructor(
    private val assignmentUseCase: AssignmentUseCase,
    private val userSessionManager: UserSessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssignmentListUiState())
    val uiState: StateFlow<AssignmentListUiState> = _uiState.asStateFlow()

    init {
        observeAssignments()
    }

    private fun observeAssignments() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val currentUser = userSessionManager.getCurrentUser()
                if (currentUser == null || currentUser.role.name != "TEACHER") {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "没有权限查看作业列表"
                    )
                    return@launch
                }

                assignmentUseCase.getAssignmentsByTeacherFlow(currentUser.id)
                    .collect { assignments ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            assignments = assignments,
                            error = null
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "加载作业列表失败"
                )
            }
        }
    }
}
