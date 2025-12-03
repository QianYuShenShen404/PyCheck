package com.example.codechecker.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codechecker.ui.screens.admin.viewmodel.SecurityViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Security Screen
 * Displays security monitoring, alerts, active sessions, and risk scanning
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(
    onNavigateBack: () -> Unit,
    viewModel: SecurityViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    var showTerminateSessionDialog by remember { mutableStateOf(false) }
    var selectedSessionId by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("安全监控") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshSecurityData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "刷新")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Success Message
            val successMsg = uiState.success
            if (successMsg != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
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
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { viewModel.clearSuccess() }) {
                            Text("关闭")
                        }
                    }
                }
            }

            // Error Message
            val errorMsg = uiState.error
            if (errorMsg != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
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

            // Security Statistics
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "安全概览",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(
                            icon = Icons.Default.Warning,
                            label = "未读警告",
                            value = uiState.securityStats.unreadAlerts.toString(),
                            color = MaterialTheme.colorScheme.primary
                        )
                        StatItem(
                            icon = Icons.Default.AccountCircle,
                            label = "活跃会话",
                            value = uiState.securityStats.activeSessions.toString(),
                            color = MaterialTheme.colorScheme.secondary
                        )
                        StatItem(
                            icon = Icons.Default.ErrorOutline,
                            label = "可疑活动",
                            value = uiState.securityStats.suspiciousActivity.toString(),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Risk Scan
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "安全风险扫描",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val lastScan = uiState.lastScanTime
                            if (lastScan != null) {
                                val format = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
                                Text(
                                    text = "上次扫描: ${format.format(Date(lastScan))}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Text(
                                    text = "尚未进行过风险扫描",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Icon(
                            Icons.Default.Security,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.performRiskScan() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isScanning
                    ) {
                        if (uiState.isScanning) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("正在扫描...")
                        } else {
                            Icon(Icons.Default.Search, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("开始扫描")
                        }
                    }
                }
            }

            // Security Alerts
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "安全警告",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { viewModel.refreshSecurityData() }) {
                            Text("全部标记为已读")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    if (uiState.securityAlerts.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "暂无安全警告",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            uiState.securityAlerts.take(5).forEach { alert ->
                                SecurityAlertCard(
                                    alert = alert,
                                    onMarkAsRead = { viewModel.markAlertAsRead(alert.id) },
                                    onClear = { viewModel.clearAlert(alert.id) }
                                )
                            }
                        }
                    }
                }
            }

            // Active Sessions
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "活跃会话",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${uiState.securityStats.activeSessions} 个会话",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (uiState.securityStats.activeSessions > 0) {
                                Button(
                                    onClick = { viewModel.terminateAllSessions() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Icon(
                                        Icons.Default.ExitToApp,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("批量登出")
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    if (uiState.activeSessions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "暂无活跃会话",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            uiState.activeSessions.take(5).forEach { session ->
                                SecuritySessionCard(
                                    session = session,
                                    onTerminate = {
                                        selectedSessionId = it
                                        showTerminateSessionDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Terminate Session Dialog
    if (showTerminateSessionDialog) {
        val session = uiState.activeSessions.find { it.id == selectedSessionId }
        AlertDialog(
            onDismissRequest = { showTerminateSessionDialog = false },
            title = { Text("终止会话") },
            text = {
                if (session != null) {
                    Column {
                        Text(
                            text = "确定要终止此会话吗？",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "用户: ${session.userEmail}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "设备: ${session.deviceInfo}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.terminateSession(selectedSessionId)
                        showTerminateSessionDialog = false
                    }
                ) {
                    Text("确认终止")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTerminateSessionDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun SecurityAlertCard(
    alert: com.example.codechecker.ui.screens.admin.viewmodel.SecurityAlert,
    onMarkAsRead: () -> Unit,
    onClear: () -> Unit
) {
    val severityColor = when (alert.severity) {
        "LOW" -> MaterialTheme.colorScheme.primary
        "MEDIUM" -> MaterialTheme.colorScheme.secondary
        "HIGH" -> MaterialTheme.colorScheme.error
        "CRITICAL" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val severityBg = when (alert.severity) {
        "LOW" -> MaterialTheme.colorScheme.primaryContainer
        "MEDIUM" -> MaterialTheme.colorScheme.secondaryContainer
        "HIGH" -> MaterialTheme.colorScheme.errorContainer
        "CRITICAL" -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val alertCardColors = if (!alert.isRead) {
        CardDefaults.cardColors(containerColor = severityBg)
    } else {
        CardDefaults.cardColors()
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = alertCardColors
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
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
                        val severityIcon = when (alert.severity) {
                            "LOW" -> Icons.Default.Info
                            "MEDIUM" -> Icons.Default.Warning
                            "HIGH" -> Icons.Default.Error
                            "CRITICAL" -> Icons.Default.Report
                            else -> Icons.Default.NotificationImportant
                        }
                        Icon(
                            severityIcon,
                            contentDescription = null,
                            tint = severityColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = getAlertTypeDisplayName(alert.type),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        if (!alert.isRead) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "未读",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = alert.message,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    val format = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
                    Text(
                        text = format.format(Date(alert.timestamp)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!alert.isRead) {
                        TextButton(onClick = onMarkAsRead) {
                            Text("标记已读", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onClear) {
                    Text("清除", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun SecuritySessionCard(
    session: com.example.codechecker.ui.screens.admin.viewmodel.SecuritySession,
    onTerminate: (String) -> Unit
) {
    val sessionCardColors = if (session.isActive) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    } else {
        CardDefaults.cardColors()
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = sessionCardColors
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        if (session.isActive) Icons.Default.AccountCircle
                        else Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = if (session.isActive)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = session.userEmail,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (session.isActive) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "活跃",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = session.deviceInfo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "IP: ${session.ipAddress}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                val format = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
                Text(
                    text = "开始: ${format.format(Date(session.startTime))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (session.isActive) {
                TextButton(onClick = { onTerminate(session.id) }) {
                    Text("终止")
                }
            }
        }
    }
}

@Composable
fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = color
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

fun getAlertTypeDisplayName(type: String): String {
    return when (type) {
        "FAILED_LOGIN" -> "登录失败"
        "SUSPICIOUS_ACTIVITY" -> "可疑活动"
        "UNUSUAL_ACCESS" -> "异常访问"
        "PASSWORD_BREACH" -> "密码泄露"
        "SESSION_ANOMALY" -> "会话异常"
        "MULTIPLE_FAILED_ATTEMPTS" -> "多次失败尝试"
        "UNKNOWN_DEVICE" -> "未知设备"
        "GEOGRAPHY_ANOMALY" -> "地理异常"
        else -> type
    }
}
