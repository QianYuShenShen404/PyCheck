package com.example.codechecker.ui.screens.assignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechecker.domain.model.Assignment
import com.example.codechecker.domain.usecase.AssignmentUseCase
import com.example.codechecker.domain.usecase.SubmissionUseCase
import com.example.codechecker.domain.usecase.PlagiarismUseCase
import com.example.codechecker.data.preference.UserSessionManager
import com.example.codechecker.domain.model.Role
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for assignment detail screen
 */
data class AssignmentDetailUiState(
    val isLoading: Boolean = false,
    val assignment: Assignment? = null,
    val submissionCount: Int = 0,
    val error: String? = null,
    val isGeneratingReport: Boolean = false,
    val generateProgress: Float = 0f,
    val generatedReportId: Long? = null,
    val generateError: String? = null,
    val autoTriggered: Boolean = false
)

/**
 * ViewModel for assignment detail screen
 */
@HiltViewModel
class AssignmentDetailViewModel @Inject constructor(
    private val assignmentUseCase: AssignmentUseCase,
    private val submissionUseCase: SubmissionUseCase,
    private val plagiarismUseCase: PlagiarismUseCase,
    private val userSessionManager: UserSessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssignmentDetailUiState())
    val uiState: StateFlow<AssignmentDetailUiState> = _uiState.asStateFlow()

    fun loadAssignment(assignmentId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val assignment = assignmentUseCase.getAssignmentById(assignmentId)
                val submissionCount = submissionUseCase.getSubmissionCountByAssignment(assignmentId)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    assignment = assignment,
                    submissionCount = submissionCount,
                    error = null
                )

                if (assignment != null) {
                    val due = assignment.dueDate ?: 0L
                    val now = System.currentTimeMillis()
                    if (due > 0 && due < now && !_uiState.value.autoTriggered) {
                        tryAutoGenerateLatestReport(assignmentId)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    assignment = null,
                    submissionCount = 0,
                    error = e.message ?: "加载作业失败"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun generateReportLatestOnly(assignmentId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGeneratingReport = true, generateProgress = 0f, generateError = null, generatedReportId = null)
            try {
                val user = userSessionManager.getCurrentUser()
                val executorId = user?.id ?: 0L
                plagiarismUseCase.generateReportLatestOnly(
                    assignmentId = assignmentId,
                    executorId = executorId,
                    progressCallback = { p ->
                        _uiState.value = _uiState.value.copy(generateProgress = p)
                    }
                ).collect { result ->
                    result.fold(
                        onSuccess = { reportId ->
                            if (reportId > 0) {
                                _uiState.value = _uiState.value.copy(isGeneratingReport = false, generatedReportId = reportId)
                            }
                        },
                        onFailure = { ex ->
                            _uiState.value = _uiState.value.copy(isGeneratingReport = false, generateError = ex.message ?: "生成报告失败")
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isGeneratingReport = false, generateError = e.message ?: "生成报告失败")
            }
        }
    }

    fun generateReportAllHistory(assignmentId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGeneratingReport = true, generateProgress = 0f, generateError = null, generatedReportId = null)
            try {
                val user = userSessionManager.getCurrentUser()
                val executorId = user?.id ?: 0L
                if (user == null || user.role != Role.TEACHER) {
                    _uiState.value = _uiState.value.copy(isGeneratingReport = false, generateError = "没有权限生成报告")
                    return@launch
                }
                plagiarismUseCase.generateReportFast(
                    assignmentId = assignmentId,
                    executorId = executorId,
                    progressCallback = { p ->
                        _uiState.value = _uiState.value.copy(generateProgress = p)
                    }
                ).collect { result ->
                    result.fold(
                        onSuccess = { reportId ->
                            if (reportId > 0) {
                                _uiState.value = _uiState.value.copy(isGeneratingReport = false, generatedReportId = reportId)
                            }
                        },
                        onFailure = { ex ->
                            _uiState.value = _uiState.value.copy(isGeneratingReport = false, generateError = ex.message ?: "生成报告失败")
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isGeneratingReport = false, generateError = e.message ?: "生成报告失败")
            }
        }
    }

    fun generateStudentLatestReport(assignmentId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGeneratingReport = true, generateProgress = 0f, generateError = null, generatedReportId = null)
            try {
                val user = userSessionManager.getCurrentUser()
                if (user == null || user.role != Role.STUDENT) {
                    _uiState.value = _uiState.value.copy(isGeneratingReport = false, generateError = "仅学生可生成个人查重报告")
                    return@launch
                }
                val result = plagiarismUseCase.generateStudentLatestTargetReport(assignmentId, user.id) { p ->
                    _uiState.value = _uiState.value.copy(generateProgress = p)
                }
                result.fold(
                    onSuccess = { pair ->
                        _uiState.value = _uiState.value.copy(isGeneratingReport = false, generatedReportId = pair.first)
                    },
                    onFailure = { ex ->
                        _uiState.value = _uiState.value.copy(isGeneratingReport = false, generateError = ex.message ?: "生成报告失败")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isGeneratingReport = false, generateError = e.message ?: "生成报告失败")
            }
        }
    }

    private suspend fun tryAutoGenerateLatestReport(assignmentId: Long) {
        try {
            val reports = plagiarismUseCase.getReportsByAssignment(assignmentId)
            val allSubs = submissionUseCase.getAllSubmissionsByAssignment(assignmentId)
            val latest = allSubs.groupBy { it.studentId }.values.mapNotNull { it.maxByOrNull { s -> s.submittedAt } }
            val expectedPairs = (latest.size * (latest.size - 1)) / 2
            val exists = reports.any { it.totalSubmissions == latest.size && it.totalPairs == expectedPairs }
            if (!exists) {
                val user = userSessionManager.getCurrentUser()
                val executorId = user?.id ?: 0L
                plagiarismUseCase.generateReportLatestOnly(
                    assignmentId = assignmentId,
                    executorId = executorId,
                    progressCallback = { p ->
                        _uiState.value = _uiState.value.copy(generateProgress = p)
                    }
                ).collect { result ->
                    result.fold(
                        onSuccess = { reportId ->
                            if (reportId > 0) {
                                _uiState.value = _uiState.value.copy(generatedReportId = reportId)
                            }
                        },
                        onFailure = { }
                    )
                }
            }
        } finally {
            _uiState.value = _uiState.value.copy(autoTriggered = true)
        }
    }
}
