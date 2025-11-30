package com.example.codechecker.ui.screens.assignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechecker.data.preference.UserSessionManager
import com.example.codechecker.domain.usecase.AssignmentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * UI state for create assignment screen
 */
data class CreateAssignmentUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val createdAssignmentId: Long? = null,
    val error: String? = null
)

/**
 * ViewModel for create assignment screen
 */
@HiltViewModel
class CreateAssignmentViewModel @Inject constructor(
    private val assignmentUseCase: AssignmentUseCase,
    private val userSessionManager: UserSessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateAssignmentUiState())
    val uiState: StateFlow<CreateAssignmentUiState> = _uiState.asStateFlow()

    private val _dueDateError = MutableStateFlow<String?>(null)
    val dueDateError: StateFlow<String?> = _dueDateError.asStateFlow()

    private val _titleError = MutableStateFlow<String?>(null)
    val titleError: StateFlow<String?> = _titleError.asStateFlow()

    fun createAssignment(
        title: String,
        description: String,
        dueDateText: String,
        submissionLimit: Int,
        pythonVersion: String
    ) {
        viewModelScope.launch {
            _dueDateError.value = null
            _titleError.value = null

            // Validate title
            if (title.isBlank()) {
                _titleError.value = "作业标题不能为空"
                return@launch
            }
            if (title.length > 100) {
                _titleError.value = "作业标题不能超过100个字符"
                return@launch
            }

            // Validate description
            if (description.length > 500) {
                _uiState.value = _uiState.value.copy(
                    error = "作业描述不能超过500个字符"
                )
                return@launch
            }

            // Validate due date if provided
            var dueDate: Long? = null
            if (dueDateText.isNotBlank()) {
                dueDate = parseDueDate(dueDateText)
                if (dueDate == null) {
                    _dueDateError.value = "日期格式不正确，请使用 yyyy-MM-dd HH:mm"
                    return@launch
                }
                if (dueDate <= System.currentTimeMillis()) {
                    _dueDateError.value = "截止时间必须晚于当前时间"
                    return@launch
                }
            }

            // Get current user
            val currentUser = userSessionManager.getCurrentUser()
            if (currentUser == null || currentUser.role.name != "TEACHER") {
                _uiState.value = _uiState.value.copy(
                    error = "没有权限创建作业"
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val result = assignmentUseCase.createAssignment(
                    title = title,
                    description = description,
                    teacherId = currentUser.id,
                    dueDate = dueDate,
                    submissionLimit = submissionLimit,
                    pythonVersion = pythonVersion
                )

                result.fold(
                    onSuccess = { assignmentId ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            createdAssignmentId = assignmentId,
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = false,
                            error = exception.message ?: "创建作业失败"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = false,
                    error = e.message ?: "创建作业失败，请重试"
                )
            }
        }
    }

    private fun parseDueDate(dateText: String): Long? {
        return try {
            val formats = listOf(
                "yyyy-MM-dd HH:mm",
                "yyyy-MM-dd",
                "yyyy/MM/dd HH:mm",
                "yyyy/MM/dd"
            )

            for (format in formats) {
                try {
                    val sdf = SimpleDateFormat(format, Locale.getDefault())
                    val date = sdf.parse(dateText)
                    if (date != null) {
                        return date.time
                    }
                } catch (e: Exception) {
                    // Try next format
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    fun clearState() {
        _uiState.value = CreateAssignmentUiState()
    }
}
