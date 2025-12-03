package com.example.codechecker.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codechecker.domain.model.AdminAuditLog
import com.example.codechecker.ui.screens.admin.viewmodel.AuditLogsViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Audit Logs Screen
 * Displays audit log entries with filtering, search, and export functionality
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditLogsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AuditLogsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var showExportDialog by remember { mutableStateOf(false) }
    var showDateRangePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("审计日志") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val csv = viewModel.exportLogs()
                        // TODO: Save to file or share
                        scope.launch {
                            viewModel.clearError()
                        }
                    }) {
                        Icon(Icons.Default.Download, contentDescription = "导出日志")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Error Message
            val errorMsg = uiState.error
            if (errorMsg != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
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
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("关闭")
                        }
                    }
                }
            }

            // Loading State
            if (uiState.isLoading && uiState.logs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Search Bar
                    item {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.searchLogs(it) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("搜索操作或详情") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            trailingIcon = if (uiState.searchQuery.isNotEmpty()) {
                                {
                                    IconButton(onClick = { viewModel.searchLogs("") }) {
                                        Icon(Icons.Default.Close, contentDescription = "清除")
                                    }
                                }
                            } else null,
                            singleLine = true
                        )
                    }

                    // Filter Chips
                    item {
                        FilterChipsSection(
                            selectedAction = uiState.selectedAction,
                            selectedResult = uiState.selectedResult,
                            availableActions = viewModel.getAvailableActions(),
                            availableResults = viewModel.getAvailableResults(),
                            onActionSelected = { viewModel.filterByAction(it) },
                            onResultSelected = { viewModel.filterByResult(it) },
                            onDateRangeClick = { showDateRangePicker = true },
                            startDate = uiState.startDate,
                            endDate = uiState.endDate
                        )
                    }

                    // Stats
                    item {
                        Card {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "总记录数",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = uiState.logs.size.toString(),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "当前页",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${uiState.currentPage + 1} / ${uiState.totalPages}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Audit Log Entries
                    items(
                        items = uiState.logs,
                        key = { it.id }
                    ) { log ->
                        AuditLogEntryCard(log)
                    }

                    // Empty State
                    if (!uiState.isLoading && uiState.logs.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Default.History,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "暂无审计日志",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "当前筛选条件下没有找到记录",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Loading More
                    if (uiState.isLoading && uiState.logs.isNotEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }

    // Date Range Picker Dialog
    if (showDateRangePicker) {
        var startDateText by remember { mutableStateOf("") }
        var endDateText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showDateRangePicker = false },
            title = { Text("日期范围筛选") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "设置筛选的日期范围（留空表示不限制）",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = startDateText,
                        onValueChange = { startDateText = it },
                        label = { Text("开始日期 (YYYY-MM-DD)") },
                        placeholder = { Text("例如: 2025-01-01") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = endDateText,
                        onValueChange = { endDateText = it },
                        label = { Text("结束日期 (YYYY-MM-DD)") },
                        placeholder = { Text("例如: 2025-12-31") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val startDateMillis = try {
                            if (startDateText.isNotBlank()) {
                                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    .parse(startDateText)?.time
                            } else null
                        } catch (e: Exception) {
                            null
                        }
                        val endDateMillis = try {
                            if (endDateText.isNotBlank()) {
                                val endOfDay = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    .parse(endDateText)
                                endOfDay?.time?.plus(24 * 60 * 60 * 1000 - 1)
                            } else null
                        } catch (e: Exception) {
                            null
                        }
                        viewModel.setDateRange(startDateMillis, endDateMillis)
                        showDateRangePicker = false
                    }
                ) {
                    Text("应用")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateRangePicker = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterChipsSection(
    selectedAction: String,
    selectedResult: String,
    availableActions: List<String>,
    availableResults: List<String>,
    onActionSelected: (String) -> Unit,
    onResultSelected: (String) -> Unit,
    onDateRangeClick: () -> Unit,
    startDate: Long?,
    endDate: Long?
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            availableActions.forEach { action ->
                FilterChip(
                    onClick = { onActionSelected(action) },
                    label = { Text(getActionDisplayName(action)) },
                    selected = selectedAction == action
                )
            }
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            availableResults.forEach { result ->
                FilterChip(
                    onClick = { onResultSelected(result) },
                    label = { Text(getResultDisplayName(result)) },
                    selected = selectedResult == result
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                onClick = onDateRangeClick,
                label = {
                    Text(
                        if (startDate != null || endDate != null) {
                            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val startStr = startDate?.let { format.format(Date(it)) } ?: "开始"
                            val endStr = endDate?.let { format.format(Date(it)) } ?: "结束"
                            "日期: $startStr ~ $endStr"
                        } else {
                            "所有日期"
                        }
                    )
                },
                selected = startDate != null || endDate != null,
                leadingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            )

            if (startDate != null || endDate != null) {
                TextButton(onClick = { onActionSelected("ALL"); onResultSelected("ALL") }) {
                    Text("清除筛选", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun AuditLogEntryCard(
    log: AdminAuditLog
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val actionDisplayName = getActionDisplayName(log.action)
                        Text(
                            text = actionDisplayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        ResultChip(result = log.result)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "管理员ID: ${log.adminUserId}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    val dateFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
                    Text(
                        text = dateFormat.format(Date(log.timestamp)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = { expanded = !expanded }) {
                        Text(if (expanded) "收起" else "详情")
                    }
                }
            }

            // Expanded Details
            if (expanded) {
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row {
                        Text(
                            text = "操作时间: ",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                .format(Date(log.timestamp)),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Row {
                        Text(
                            text = "目标类型: ",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = log.targetType,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Row {
                        Text(
                            text = "目标ID: ",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = log.targetId ?: "-",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (!log.details.isNullOrBlank()) {
                        Column {
                            Text(
                                text = "详情: ",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = log.details ?: "",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResultChip(result: String) {
    val (color, bgColor) = when (result) {
        "SUCCESS" -> Pair(
            MaterialTheme.colorScheme.onPrimary,
            MaterialTheme.colorScheme.primary
        )
        "FAILED" -> Pair(
            MaterialTheme.colorScheme.onError,
            MaterialTheme.colorScheme.error
        )
        "PARTIAL" -> Pair(
            MaterialTheme.colorScheme.onPrimaryContainer,
            MaterialTheme.colorScheme.primaryContainer
        )
        else -> Pair(
            MaterialTheme.colorScheme.onSurface,
            MaterialTheme.colorScheme.surfaceVariant
        )
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = getResultDisplayName(result),
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
        )
    }
}

fun getActionDisplayName(action: String): String {
    return when (action) {
        "ALL" -> "全部"
        "USER_CREATE" -> "创建用户"
        "USER_UPDATE" -> "更新用户"
        "USER_DELETE" -> "删除用户"
        "USER_DISABLE" -> "禁用用户"
        "USER_ENABLE" -> "启用用户"
        "USER_ROLE_CHANGE" -> "更改角色"
        "PASSWORD_RESET" -> "重置密码"
        "DATA_EXPORT" -> "导出数据"
        "DATA_IMPORT" -> "导入数据"
        "DATA_CLEANUP" -> "清理数据"
        "SETTINGS_UPDATE" -> "更新设置"
        "DATABASE_BACKUP" -> "数据库备份"
        "DATABASE_RESTORE" -> "数据库恢复"
        else -> action
    }
}

fun getResultDisplayName(result: String): String {
    return when (result) {
        "ALL" -> "全部"
        "SUCCESS" -> "成功"
        "FAILED" -> "失败"
        "PARTIAL" -> "部分成功"
        else -> result
    }
}
