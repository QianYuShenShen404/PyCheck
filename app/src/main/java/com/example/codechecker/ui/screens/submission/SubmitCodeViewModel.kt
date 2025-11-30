package com.example.codechecker.ui.screens.submission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechecker.domain.usecase.SubmissionUseCase
import com.example.codechecker.domain.usecase.PlagiarismUseCase
import com.example.codechecker.data.preference.UserSessionManager
import com.example.codechecker.domain.model.Role
import com.example.codechecker.util.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for submit code screen
 */
data class SubmitCodeUiState(
    val isLoading: Boolean = false,
    val isSubmissionSuccess: Boolean = false,
    val error: String? = null,
    val similarities: List<com.example.codechecker.domain.model.Similarity> = emptyList(),
    val newSubmissionId: Long? = null
)

/**
 * ViewModel for submit code screen
 */
@HiltViewModel
class SubmitCodeViewModel @Inject constructor(
    private val submissionUseCase: SubmissionUseCase,
    private val plagiarismUseCase: PlagiarismUseCase,
    private val userSessionManager: UserSessionManager,
    private val validationUtils: ValidationUtils
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubmitCodeUiState())
    val uiState: StateFlow<SubmitCodeUiState> = _uiState.asStateFlow()

    fun submitCodeText(
        assignmentId: Long,
        codeContent: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                if (codeContent.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSubmissionSuccess = false,
                        error = "代码内容不能为空"
                    )
                    return@launch
                }

                val currentUser = userSessionManager.getCurrentUser()
                if (currentUser == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSubmissionSuccess = false,
                        error = "未登录"
                    )
                    return@launch
                }
                if (currentUser.role != Role.STUDENT) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSubmissionSuccess = false,
                        error = "仅学生可提交代码"
                    )
                    return@launch
                }

                val result = submissionUseCase.submitCode(
                    assignmentId = assignmentId,
                    studentId = currentUser.id,
                    fileName = "submission_${System.currentTimeMillis()}.py",
                    codeContent = codeContent
                )

                result.fold(
                    onSuccess = { submissionId ->
                        val genResult = plagiarismUseCase.generateStudentLatestTargetReport(
                            assignmentId = assignmentId,
                            studentId = currentUser.id
                        )
                        val pairs = genResult.fold(
                            onSuccess = { it.second },
                            onFailure = { emptyList() }
                        )
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSubmissionSuccess = true,
                            error = null,
                            similarities = pairs,
                            newSubmissionId = submissionId
                        )
                    },
                    onFailure = { ex ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSubmissionSuccess = false,
                            error = ex.message ?: "提交失败"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSubmissionSuccess = false,
                    error = e.message ?: "提交失败"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun resetState() {
        _uiState.value = SubmitCodeUiState()
    }
}
