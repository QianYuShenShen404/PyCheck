package com.example.codechecker.ui.screens.submission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechecker.data.preference.UserSessionManager
import com.example.codechecker.domain.model.Submission
import com.example.codechecker.domain.usecase.SubmissionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for submission history screen
 */
data class SubmissionHistoryUiState(
    val isLoading: Boolean = false,
    val submissions: List<Submission> = emptyList(),
    val error: String? = null
)

/**
 * ViewModel for submission history screen
 */
@HiltViewModel
class SubmissionHistoryViewModel @Inject constructor(
    private val submissionUseCase: SubmissionUseCase,
    private val userSessionManager: UserSessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubmissionHistoryUiState())
    val uiState: StateFlow<SubmissionHistoryUiState> = _uiState.asStateFlow()

    init {
        loadSubmissions()
    }

    fun loadSubmissions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val currentUser = userSessionManager.getCurrentUser()
                val submissions = currentUser?.let { user ->
                    submissionUseCase.getSubmissionsByUser(user.id)
                } ?: emptyList()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    submissions = submissions,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    submissions = emptyList(),
                    error = e.message ?: "加载提交历史失败"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
