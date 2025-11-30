package com.example.codechecker.ui.screens.assignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechecker.domain.model.Submission
import com.example.codechecker.domain.usecase.AssignmentUseCase
import com.example.codechecker.domain.usecase.SubmissionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for submission list screen
 */
data class SubmissionListUiState(
    val submissions: List<Submission> = emptyList(),
    val assignmentTitle: String = "",
    val dueDate: Long? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for submission list screen
 */
@HiltViewModel
class SubmissionListViewModel @Inject constructor(
    private val assignmentUseCase: AssignmentUseCase,
    private val submissionUseCase: SubmissionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubmissionListUiState())
    val uiState: StateFlow<SubmissionListUiState> = _uiState.asStateFlow()

    fun loadSubmissions(assignmentId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // Load assignment details
                val assignment = assignmentUseCase.getAssignmentById(assignmentId)
                if (assignment == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "作业不存在"
                    )
                    return@launch
                }

                // Load all submissions for this assignment
                val allSubmissions = submissionUseCase.getAllSubmissionsByAssignment(assignmentId)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    submissions = allSubmissions,
                    assignmentTitle = assignment.title,
                    dueDate = assignment.dueDate,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "加载提交列表失败"
                )
            }
        }
    }
}
