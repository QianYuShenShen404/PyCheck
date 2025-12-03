package com.example.codechecker.ui.screens.admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechecker.domain.model.AdminSettings
import com.example.codechecker.domain.model.LogLevel
import com.example.codechecker.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for System Settings
 */
data class SystemSettingsUiState(
    val settings: AdminSettings = AdminSettings(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val hasUnsavedChanges: Boolean = false,
    val validationErrors: Map<String, String> = emptyMap()
)

/**
 * ViewModel for System Settings
 */
@HiltViewModel
class SystemSettingsViewModel @Inject constructor(
    private val getAdminSettingsUseCase: GetAdminSettingsUseCase,
    private val updateAdminSettingsUseCase: UpdateAdminSettingsUseCase,
    private val resetSettingsToDefaultUseCase: ResetSettingsToDefaultUseCase,
    private val exportSettingsUseCase: ExportSettingsUseCase,
    private val importSettingsUseCase: ImportSettingsUseCase,
    private val aiRepository: com.example.codechecker.domain.repository.AIRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SystemSettingsUiState())
    val uiState: StateFlow<SystemSettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    fun loadSettings() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                val settings = getAdminSettingsUseCase()
                _uiState.value = SystemSettingsUiState(
                    settings = settings,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "加载设置失败"
                )
            }
        }
    }

    fun updateSimilarityThreshold(value: Int) {
        val currentSettings = _uiState.value.settings
        val newSettings = currentSettings.copy(similarityThreshold = value)
        _uiState.value = _uiState.value.copy(
            settings = newSettings,
            hasUnsavedChanges = true,
            successMessage = null,
            error = null
        )
    }

    fun updateReportRetentionDays(value: Int) {
        val currentSettings = _uiState.value.settings
        val newSettings = currentSettings.copy(reportRetentionDays = value)
        _uiState.value = _uiState.value.copy(
            settings = newSettings,
            hasUnsavedChanges = true,
            successMessage = null,
            error = null
        )
    }

    fun updateSubmissionRetentionDays(value: Int) {
        val currentSettings = _uiState.value.settings
        val newSettings = currentSettings.copy(submissionRetentionDays = value)
        _uiState.value = _uiState.value.copy(
            settings = newSettings,
            hasUnsavedChanges = true,
            successMessage = null,
            error = null
        )
    }

    fun updateFastCompareMode(enabled: Boolean) {
        val currentSettings = _uiState.value.settings
        val newSettings = currentSettings.copy(fastCompareMode = enabled)
        _uiState.value = _uiState.value.copy(
            settings = newSettings,
            hasUnsavedChanges = true,
            successMessage = null,
            error = null
        )
    }

    fun updateLogLevel(logLevel: LogLevel) {
        val currentSettings = _uiState.value.settings
        val newSettings = currentSettings.copy(logLevel = logLevel)
        _uiState.value = _uiState.value.copy(
            settings = newSettings,
            hasUnsavedChanges = true,
            successMessage = null,
            error = null
        )
    }

    fun updateAutoCleanupEnabled(enabled: Boolean) {
        val currentSettings = _uiState.value.settings
        val newSettings = currentSettings.copy(autoCleanupEnabled = enabled)
        _uiState.value = _uiState.value.copy(
            settings = newSettings,
            hasUnsavedChanges = true,
            successMessage = null,
            error = null
        )
    }

    fun updateMaxSubmissionsPerAssignment(value: Int) {
        val currentSettings = _uiState.value.settings
        val newSettings = currentSettings.copy(maxSubmissionsPerAssignment = value)
        _uiState.value = _uiState.value.copy(
            settings = newSettings,
            hasUnsavedChanges = true,
            successMessage = null,
            error = null
        )
    }

    fun updateAiBaseUrl(value: String) {
        val current = _uiState.value.settings
        val newSettings = current.copy(aiBaseUrl = value)
        _uiState.value = _uiState.value.copy(settings = newSettings, hasUnsavedChanges = true, successMessage = null, error = null)
    }

    fun updateAiModel(value: String) {
        val current = _uiState.value.settings
        val newSettings = current.copy(aiModel = value)
        _uiState.value = _uiState.value.copy(settings = newSettings, hasUnsavedChanges = true, successMessage = null, error = null)
    }

    fun updateAiApiKey(value: String) {
        val current = _uiState.value.settings
        val newSettings = current.copy(aiApiKey = value)
        _uiState.value = _uiState.value.copy(settings = newSettings, hasUnsavedChanges = true, successMessage = null, error = null)
    }

    fun updateAiConnectTimeoutSec(value: Int) {
        val current = _uiState.value.settings
        val newSettings = current.copy(aiConnectTimeoutSec = value)
        _uiState.value = _uiState.value.copy(settings = newSettings, hasUnsavedChanges = true, successMessage = null, error = null)
    }

    fun updateAiReadTimeoutSec(value: Int) {
        val current = _uiState.value.settings
        val newSettings = current.copy(aiReadTimeoutSec = value)
        _uiState.value = _uiState.value.copy(settings = newSettings, hasUnsavedChanges = true, successMessage = null, error = null)
    }

    fun updateAiRetryTimes(value: Int) {
        val current = _uiState.value.settings
        val newSettings = current.copy(aiRetryTimes = value)
        _uiState.value = _uiState.value.copy(settings = newSettings, hasUnsavedChanges = true, successMessage = null, error = null)
    }

    fun saveSettings(adminUserId: Long = 0) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isSaving = true,
                    error = null,
                    successMessage = null
                )

                val result = updateAdminSettingsUseCase(
                    settings = _uiState.value.settings,
                    adminUserId = adminUserId
                )

                result.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            hasUnsavedChanges = false,
                            successMessage = "设置保存成功",
                            error = null
                        )
                    },
                    onFailure = {
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            error = it.message ?: "保存设置失败"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message ?: "保存设置失败"
                )
            }
        }
    }

    fun resetToDefaults(adminUserId: Long = 0) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isSaving = true,
                    error = null
                )

                val result = resetSettingsToDefaultUseCase(adminUserId)

                result.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            hasUnsavedChanges = false,
                            successMessage = "已重置为默认设置",
                            error = null
                        )
                        loadSettings()
                    },
                    onFailure = {
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            error = it.message ?: "重置设置失败"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message ?: "重置设置失败"
                )
            }
        }
    }

    suspend fun exportSettings(adminUserId: Long = 0): String? {
        return try {
            val result = exportSettingsUseCase(adminUserId)
            var exportedJson: String? = null
            result.fold(
                onSuccess = { json ->
                    exportedJson = json
                    _uiState.value = _uiState.value.copy(
                        successMessage = "设置导出成功"
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        error = it.message ?: "导出设置失败"
                    )
                }
            )
            exportedJson
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = e.message ?: "导出设置失败"
            )
            null
        }
    }

    fun importSettings(json: String, adminUserId: Long = 0) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isSaving = true,
                    error = null
                )

                val result = importSettingsUseCase(json, adminUserId)

                result.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            hasUnsavedChanges = false,
                            successMessage = "设置导入成功",
                            error = null
                        )
                        loadSettings()
                    },
                    onFailure = {
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            error = it.message ?: "导入设置失败"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message ?: "导入设置失败"
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            error = null,
            successMessage = null
        )
    }

    fun validateSettings(): Boolean {
        val errors = mutableMapOf<String, String>()
        val settings = _uiState.value.settings

        if (settings.similarityThreshold !in 0..100) {
            errors["similarityThreshold"] = "相似度阈值必须在0-100之间"
        }

        if (settings.reportRetentionDays < 0) {
            errors["reportRetentionDays"] = "报告保留天数不能为负数"
        }

        if (settings.submissionRetentionDays < 0) {
            errors["submissionRetentionDays"] = "提交保留天数不能为负数"
        }

        if (settings.maxSubmissionsPerAssignment <= 0) {
            errors["maxSubmissionsPerAssignment"] = "最大提交数必须大于0"
        }

        if (!settings.aiBaseUrl.startsWith("http")) {
            errors["aiBaseUrl"] = "AI接口地址格式不正确"
        }
        if (settings.aiConnectTimeoutSec !in 1..120) {
            errors["aiConnectTimeoutSec"] = "连接超时应在1-120秒"
        }
        if (settings.aiReadTimeoutSec !in 1..180) {
            errors["aiReadTimeoutSec"] = "读取超时应在1-180秒"
        }
        if (settings.aiRetryTimes !in 1..10) {
            errors["aiRetryTimes"] = "重试次数应在1-10之间"
        }

        _uiState.value = _uiState.value.copy(validationErrors = errors)
        return errors.isEmpty()
    }

    fun testAiConnection() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isSaving = true, error = null, successMessage = null)
                val res = aiRepository.analyze("def f():\n    return 1", "def g():\n    return 1", 80.0)
                when (res) {
                    is com.example.codechecker.domain.model.AIAnalysisResult.Success -> {
                        _uiState.value = _uiState.value.copy(isSaving = false, successMessage = "AI连接正常")
                    }
                    is com.example.codechecker.domain.model.AIAnalysisResult.Error -> {
                        _uiState.value = _uiState.value.copy(isSaving = false, error = "AI连接失败: ${res.message}")
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = "AI连接超时")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = e.message ?: "AI连接失败")
            }
        }
    }
}
