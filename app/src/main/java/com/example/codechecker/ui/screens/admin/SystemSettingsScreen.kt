package com.example.codechecker.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codechecker.domain.model.LogLevel
import com.example.codechecker.ui.screens.admin.viewmodel.SystemSettingsViewModel
import kotlinx.coroutines.launch

/**
 * System Settings Screen
 * Allows administrators to configure system parameters and algorithm settings
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SystemSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("系统设置") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(Icons.Default.Restore, contentDescription = "重置为默认")
                    }
                    IconButton(onClick = { showExportDialog = true }) {
                        Icon(Icons.Default.Download, contentDescription = "导出设置")
                    }
                    IconButton(onClick = { showImportDialog = true }) {
                        Icon(Icons.Default.Upload, contentDescription = "导入设置")
                    }
                    if (uiState.hasUnsavedChanges) {
                        TextButton(
                            onClick = {
                                if (viewModel.validateSettings()) {
                                    viewModel.saveSettings(0) // TODO: Get current admin ID
                                }
                            }
                        ) {
                            Text("保存")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Success/Error Messages
            val errorMsg = uiState.error
            if (errorMsg != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMsg,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            val successMsg = uiState.successMessage
            if (successMsg != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = successMsg,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Loading State
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Similarity Threshold Section
                SettingsSection(title = "算法设置") {
                    SimilarityThresholdCard(uiState.settings.similarityThreshold) { threshold ->
                        viewModel.updateSimilarityThreshold(threshold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    FastCompareModeCard(uiState.settings.fastCompareMode) { enabled ->
                        viewModel.updateFastCompareMode(enabled)
                    }
                }

                // Retention Policy Section
                SettingsSection(title = "保留策略") {
                    ReportRetentionCard(uiState.settings.reportRetentionDays) { days ->
                        viewModel.updateReportRetentionDays(days)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    SubmissionRetentionCard(uiState.settings.submissionRetentionDays) { days ->
                        viewModel.updateSubmissionRetentionDays(days)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    MaxSubmissionsCard(uiState.settings.maxSubmissionsPerAssignment) { max ->
                        viewModel.updateMaxSubmissionsPerAssignment(max)
                    }
                }

                // System Settings Section
                SettingsSection(title = "系统设置") {
                    AutoCleanupCard(uiState.settings.autoCleanupEnabled) { enabled ->
                        viewModel.updateAutoCleanupEnabled(enabled)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LogLevelCard(uiState.settings.logLevel) { level ->
                        viewModel.updateLogLevel(level)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                SettingsSection(title = "AI设置") {
                    AiBaseUrlCard(uiState.settings.aiBaseUrl) { url ->
                        viewModel.updateAiBaseUrl(url)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    AiModelCard(uiState.settings.aiModel) { model ->
                        viewModel.updateAiModel(model)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    AiApiKeyCard(uiState.settings.aiApiKey) { key ->
                        viewModel.updateAiApiKey(key)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    AiTimeoutRetryCard(
                        connectTimeout = uiState.settings.aiConnectTimeoutSec,
                        readTimeout = uiState.settings.aiReadTimeoutSec,
                        retryTimes = uiState.settings.aiRetryTimes,
                        onConnectTimeoutChange = { viewModel.updateAiConnectTimeoutSec(it) },
                        onReadTimeoutChange = { viewModel.updateAiReadTimeoutSec(it) },
                        onRetryTimesChange = { viewModel.updateAiRetryTimes(it) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(onClick = { viewModel.testAiConnection() }) {
                            Text("连接测试")
                        }
                    }
                }

                // Save Button
                if (uiState.hasUnsavedChanges) {
                    Button(
                        onClick = {
                            if (viewModel.validateSettings()) {
                                scope.launch {
                                    viewModel.saveSettings(0) // TODO: Get current admin ID
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSaving
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("保存设置")
                    }
                }
            }
        }
    }

    // Export Dialog
    if (showExportDialog) {
        ExportSettingsDialog(
            onDismiss = { showExportDialog = false },
            onExport = { json ->
                // TODO: Save to file or share
                showExportDialog = false
                scope.launch {
                    viewModel.exportSettings(0) // TODO: Get current admin ID
                }
            }
        )
    }

    // Import Dialog
    if (showImportDialog) {
        ImportSettingsDialog(
            onDismiss = { showImportDialog = false },
            onImport = { json ->
                scope.launch {
                    viewModel.importSettings(json, 0) // TODO: Get current admin ID
                }
                showImportDialog = false
            }
        )
    }

    // Reset Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("重置设置") },
            text = { Text("确定要重置所有设置为默认值吗？此操作无法撤销。") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        viewModel.resetToDefaults(0) // TODO: Get current admin ID
                    }
                    showResetDialog = false
                }) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            content()
        }
    }
}

@Composable
fun SimilarityThresholdCard(
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "相似度阈值",
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = "当代码相似度超过此阈值时，将标记为可疑",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Slider(
                value = value.toFloat(),
                onValueChange = { onValueChange(it.toInt()) },
                valueRange = 0f..100f,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$value%",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.width(48.dp)
            )
        }
    }
}

@Composable
fun FastCompareModeCard(
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "快速比对模式",
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "启用后将跳过标识符标准化，提高比对速度但可能降低准确性",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = enabled, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun ReportRetentionCard(
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "报告保留天数",
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = "报告保留多少天后自动删除",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = value.toString(),
                onValueChange = {
                    val intValue = it.toIntOrNull() ?: 0
                    if (intValue >= 0) onValueChange(intValue)
                },
                modifier = Modifier.weight(1f),
                label = { Text("天数") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )
        }
    }
}

@Composable
fun SubmissionRetentionCard(
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "提交保留天数",
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = "代码提交保留多少天后自动删除",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = value.toString(),
                onValueChange = {
                    val intValue = it.toIntOrNull() ?: 0
                    if (intValue >= 0) onValueChange(intValue)
                },
                modifier = Modifier.weight(1f),
                label = { Text("天数") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )
        }
    }
}

@Composable
fun MaxSubmissionsCard(
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "每作业最大提交数",
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = "每个作业允许的最大提交数量",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = value.toString(),
                onValueChange = {
                    val intValue = it.toIntOrNull() ?: 0
                    if (intValue > 0) onValueChange(intValue)
                },
                modifier = Modifier.weight(1f),
                label = { Text("数量") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )
        }
    }
}

@Composable
fun AutoCleanupCard(
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "自动清理",
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "启用后将根据保留策略自动清理旧数据",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = enabled, onCheckedChange = onCheckedChange)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogLevelCard(
    logLevel: LogLevel,
    onValueChange: (LogLevel) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "日志级别",
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = "系统日志的记录级别",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = when (logLevel) {
                    LogLevel.DEBUG -> "调试"
                    LogLevel.INFO -> "信息"
                    LogLevel.WARN -> "警告"
                    LogLevel.ERROR -> "错误"
                },
                onValueChange = { },
                label = { Text("日志级别") },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("调试") },
                    onClick = {
                        onValueChange(LogLevel.DEBUG)
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("信息") },
                    onClick = {
                        onValueChange(LogLevel.INFO)
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("警告") },
                    onClick = {
                        onValueChange(LogLevel.WARN)
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("错误") },
                    onClick = {
                        onValueChange(LogLevel.ERROR)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun AiBaseUrlCard(value: String, onValueChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "AI接口地址", style = MaterialTheme.typography.titleSmall)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Base URL") },
            singleLine = true
        )
        Text(
            text = "例如：https://api.siliconflow.cn/v1",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AiModelCard(value: String, onValueChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "模型名称", style = MaterialTheme.typography.titleSmall)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Model") },
            singleLine = true
        )
        Text(
            text = "例如：Qwen/Qwen2.5-72B-Instruct",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AiApiKeyCard(value: String, onValueChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "API Key", style = MaterialTheme.typography.titleSmall)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("密钥") },
            singleLine = true,
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
        )
        Text(
            text = "仅管理员可见并修改，保存后将加密存储",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AiTimeoutRetryCard(
    connectTimeout: Int,
    readTimeout: Int,
    retryTimes: Int,
    onConnectTimeoutChange: (Int) -> Unit,
    onReadTimeoutChange: (Int) -> Unit,
    onRetryTimesChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "超时与重试", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = connectTimeout.toString(),
                onValueChange = { it.toIntOrNull()?.let(onConnectTimeoutChange) },
                label = { Text("连接超时(秒)") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = readTimeout.toString(),
                onValueChange = { it.toIntOrNull()?.let(onReadTimeoutChange) },
                label = { Text("读取超时(秒)") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = retryTimes.toString(),
                onValueChange = { it.toIntOrNull()?.let(onRetryTimesChange) },
                label = { Text("重试次数") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        }
    }
}

@Composable
fun ExportSettingsDialog(
    onDismiss: () -> Unit,
    onExport: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("导出设置") },
        text = { Text("确定要导出当前设置吗？设置将保存为JSON格式。") },
        confirmButton = {
            TextButton(onClick = {
                onExport("JSON")
                onDismiss()
            }) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun ImportSettingsDialog(
    onDismiss: () -> Unit,
    onImport: (String) -> Unit
) {
    var jsonText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("导入设置") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("请粘贴要导入的JSON设置：")
                OutlinedTextField(
                    value = jsonText,
                    onValueChange = { jsonText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    label = { Text("JSON设置") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (jsonText.isNotBlank()) {
                        onImport(jsonText)
                    }
                },
                enabled = jsonText.isNotBlank()
            ) {
                Text("导入")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
