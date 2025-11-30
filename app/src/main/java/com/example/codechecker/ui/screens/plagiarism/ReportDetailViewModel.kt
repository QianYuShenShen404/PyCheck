package com.example.codechecker.ui.screens.plagiarism

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechecker.domain.model.Report
import com.example.codechecker.domain.model.Similarity
import com.example.codechecker.domain.model.Submission
import com.example.codechecker.domain.repository.SubmissionRepository
import com.example.codechecker.domain.repository.UserRepository
import com.example.codechecker.domain.usecase.PlagiarismUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for report detail screen
 */
data class ReportDetailUiState(
    val report: Report? = null,
    val similarities: List<Similarity> = emptyList(),
    val highSimilarities: List<Similarity> = emptyList(),
    val submissionsById: Map<Long, Submission> = emptyMap(),
    val latestStudentCount: Int = 0,
    val totalSubmissionCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for report detail screen
 */
@HiltViewModel
class ReportDetailViewModel @Inject constructor(
    private val plagiarismUseCase: PlagiarismUseCase,
    private val submissionRepository: SubmissionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportDetailUiState())
    val uiState: StateFlow<ReportDetailUiState> = _uiState.asStateFlow()

    fun loadReport(reportId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val report = plagiarismUseCase.getReportById(reportId)
                val similarities = plagiarismUseCase.getSimilaritiesByReport(reportId)
                val highSimilarities = plagiarismUseCase.getHighSimilarityPairs(reportId, threshold = 60f)

                val submissionIds = similarities.flatMap { listOf(it.submission1Id, it.submission2Id) }.distinct()
                val submissionsMap = mutableMapOf<Long, Submission>()
                for (sid in submissionIds) {
                    val sub = submissionRepository.getSubmissionById(sid)
                    if (sub != null) {
                        val user = userRepository.getUserById(sub.studentId)
                        val enriched = if (user != null) sub.copy(studentName = user.displayName) else sub
                        submissionsMap[sid] = enriched
                    }
                }

                val assignmentId = report?.assignmentId
                val allSubs = assignmentId?.let { submissionRepository.getAllSubmissionsByAssignment(it) } ?: emptyList()
                val latestCount = allSubs.groupBy { it.studentId }.size
                val totalCount = allSubs.size

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    report = report,
                    similarities = similarities,
                    highSimilarities = highSimilarities,
                    submissionsById = submissionsMap,
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
