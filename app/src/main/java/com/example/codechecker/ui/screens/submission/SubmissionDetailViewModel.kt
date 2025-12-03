package com.example.codechecker.ui.screens.submission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechecker.data.preference.UserSessionManager
import com.example.codechecker.domain.model.Role
import com.example.codechecker.domain.model.Submission
import com.example.codechecker.domain.usecase.SubmissionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SubmissionDetailUiState(
    val isLoading: Boolean = false,
    val submission: Submission? = null,
    val error: String? = null
)

@HiltViewModel
class SubmissionDetailViewModel @Inject constructor(
    private val submissionUseCase: SubmissionUseCase,
    private val userSessionManager: UserSessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubmissionDetailUiState())
    val uiState: StateFlow<SubmissionDetailUiState> = _uiState.asStateFlow()

    fun loadSubmission(submissionId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val submission = submissionUseCase.getSubmissionById(submissionId)
                val currentUser = userSessionManager.getCurrentUser()
                if (submission == null) {
                    _uiState.value = SubmissionDetailUiState(isLoading = false, submission = null, error = "提交不存在")
                    return@launch
                }

                val role = currentUser?.role
                if (role == Role.STUDENT && currentUser.id != submission.studentId) {
                    _uiState.value = SubmissionDetailUiState(isLoading = false, submission = null, error = "无权限查看该提交")
                } else {
                    _uiState.value = SubmissionDetailUiState(isLoading = false, submission = submission, error = null)
                }
            } catch (e: Exception) {
                _uiState.value = SubmissionDetailUiState(isLoading = false, submission = null, error = e.message ?: "加载失败")
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

