package com.example.codechecker.ui.screens.plagiarism

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codechecker.R
import com.example.codechecker.domain.model.Report
import com.example.codechecker.ui.components.LoadingIndicator
import com.example.codechecker.ui.navigation.createPlagiarismReportRoute
import com.example.codechecker.di.UtilityModuleEntryPoint
import dagger.hilt.android.EntryPointAccessors

/**
 * Screen showing list of plagiarism reports
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportListScreen(
    assignmentId: Long?,
    onNavigateBack: () -> Unit,
    onNavigateToReportDetail: (Long) -> Unit,
    viewModel: ReportListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    val userSessionManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            UtilityModuleEntryPoint::class.java
        ).userSessionManager()
    }
    val currentUser by userSessionManager.currentUser.collectAsStateWithLifecycle(initialValue = null)

    LaunchedEffect(assignmentId) {
        assignmentId?.let {
            viewModel.loadReports(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("查重报告") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                LoadingIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.reports.isEmpty()) {
                Box(
                    modifier = Modifier.align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无查重报告",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                var selected by remember { mutableStateOf(0) }
                val latestCount = uiState.latestStudentCount
                val totalCount = uiState.totalSubmissionCount
                val isTeacher = currentUser?.role == com.example.codechecker.domain.model.Role.TEACHER
                val allReports = uiState.reports
                val filteredReports = remember(allReports, selected, latestCount, totalCount, isTeacher, currentUser?.id) {
                    val latestPairs = latestCount * (latestCount - 1) / 2
                    val allPairs = totalCount * (totalCount - 1) / 2
                    val base = if (!isTeacher) {
                        allReports.filter { it.executorId == (currentUser?.id ?: -1) }
                    } else allReports
                    when (selected) {
                        0 -> base
                        1 -> base.filter { it.totalSubmissions == latestCount && it.totalPairs == latestPairs }
                        2 -> base.filter { it.totalSubmissions == totalCount && it.totalPairs == allPairs }
                        else -> base.filter { it.totalSubmissions == latestCount && it.totalPairs == latestCount - 1 }
                    }
                }

                if (isTeacher) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = selected == 0,
                            onClick = { selected = 0 },
                            label = { Text("全部") }
                        )
                        FilterChip(
                            selected = selected == 1,
                            onClick = { selected = 1 },
                            label = { Text("仅最后一次") }
                        )
                        FilterChip(
                            selected = selected == 2,
                            onClick = { selected = 2 },
                            label = { Text("所有历史") }
                        )
                        FilterChip(
                            selected = selected == 3,
                            onClick = { selected = 3 },
                            label = { Text("个人报告") }
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredReports) { report ->
                        ReportCard(
                            report = report,
                            latestStudentCount = uiState.latestStudentCount,
                            totalSubmissionCount = uiState.totalSubmissionCount,
                            onClick = { onNavigateToReportDetail(report.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportCard(
    report: Report,
    latestStudentCount: Int,
    totalSubmissionCount: Int,
    onClick: () -> Unit
) {
    @OptIn(ExperimentalMaterial3Api::class)
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "报告 #${report.id}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "提交数: ${report.totalSubmissions}, 对比数: ${report.totalPairs}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val scopeText = when {
                        report.totalSubmissions == latestStudentCount -> "范围：仅最后一次提交"
                        report.totalSubmissions == totalSubmissionCount -> "范围：所有历史提交"
                        else -> "范围：学生个人最新比对"
                    }
                    Text(
                        text = scopeText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = when (report.status) {
                        com.example.codechecker.domain.model.ReportStatus.PENDING -> "进行中"
                        com.example.codechecker.domain.model.ReportStatus.COMPLETED -> "已完成"
                        else -> report.status.name
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = if (report.completedAt != null) {
                        "完成时间: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(java.util.Date(report.completedAt))}"
                    } else {
                        "创建时间: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(java.util.Date(report.createdAt))}"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
