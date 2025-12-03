package com.example.codechecker.ui.screens.plagiarism

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechecker.domain.model.Similarity
import com.example.codechecker.domain.usecase.PlagiarismUseCase
import com.example.codechecker.domain.repository.SubmissionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for code comparison screen
 */
data class CompareCodeUiState(
    val similarity: Similarity? = null,
    val code1: String = "",
    val code2: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val similarityThreshold: Int = 0,
    val aiLoading: Boolean = false,
    val aiError: String? = null,
    val aiResult: com.example.codechecker.domain.model.AIAnalysisResult? = null
)

/**
 * ViewModel for code comparison screen
 */
@HiltViewModel
class CompareCodeViewModel @Inject constructor(
    private val plagiarismUseCase: PlagiarismUseCase,
    private val submissionRepository: SubmissionRepository,
    private val getAdminSettingsUseCase: com.example.codechecker.domain.usecase.GetAdminSettingsUseCase,
    private val aiRepository: com.example.codechecker.domain.repository.AIRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompareCodeUiState())
    val uiState: StateFlow<CompareCodeUiState> = _uiState.asStateFlow()

    fun loadSimilarity(similarityId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val similarity = plagiarismUseCase.getSimilarityById(similarityId)

                if (similarity != null) {
                    val submission1 = submissionRepository.getSubmissionById(similarity.submission1Id)
                    val submission2 = submissionRepository.getSubmissionById(similarity.submission2Id)
                    val settings = getAdminSettingsUseCase()

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        similarity = similarity,
                        code1 = submission1?.codeContent ?: "",
                        code2 = submission2?.codeContent ?: "",
                        similarityThreshold = settings.similarityThreshold
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "未找到相似度数据"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "加载失败"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun analyzeWithAI() {
        val s = _uiState.value.similarity ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(aiLoading = true, aiError = null, aiResult = null)
            try {
                val res = aiRepository.analyze(_uiState.value.code1, _uiState.value.code2, s.similarityScore.toDouble())
                _uiState.value = _uiState.value.copy(aiLoading = false, aiResult = res)
            } catch (e: java.net.SocketTimeoutException) {
                _uiState.value = _uiState.value.copy(aiLoading = false, aiError = "timeout")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(aiLoading = false, aiError = e.message ?: "分析失败")
            }
        }
    }

    fun clearAiError() {
        _uiState.value = _uiState.value.copy(aiError = null)
    }
}
