package com.example.codechecker.ui.screens.plagiarism

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechecker.domain.model.Report
import com.example.codechecker.domain.usecase.PlagiarismUseCase
import com.example.codechecker.domain.usecase.SubmissionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for report list screen
 */
data class ReportListUiState(
    val reports: List<Report> = emptyList(),
    val latestStudentCount: Int = 0,
    val totalSubmissionCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for report list screen
 */
@HiltViewModel
class ReportListViewModel @Inject constructor(
    private val plagiarismUseCase: PlagiarismUseCase,
    private val submissionUseCase: SubmissionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportListUiState())
    val uiState: StateFlow<ReportListUiState> = _uiState.asStateFlow()

    fun loadReports(assignmentId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val reports = plagiarismUseCase.getReportsByAssignment(assignmentId)
                val allSubmissions = submissionUseCase.getAllSubmissionsByAssignment(assignmentId)
                val latestCount = allSubmissions.groupBy { it.studentId }.size
                val totalCount = allSubmissions.size
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    reports = reports,
                    latestStudentCount = latestCount,
                    totalSubmissionCount = totalCount
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "加载报告失败"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
