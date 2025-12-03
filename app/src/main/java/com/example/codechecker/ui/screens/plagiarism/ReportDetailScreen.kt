package com.example.codechecker.ui.screens.plagiarism

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Warning
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
import com.example.codechecker.domain.model.Similarity
import com.example.codechecker.domain.model.Submission
import com.example.codechecker.ui.components.LoadingIndicator
import com.example.codechecker.ui.navigation.createCodeComparisonRoute
import com.example.codechecker.di.UtilityModuleEntryPoint
import dagger.hilt.android.EntryPointAccessors

/**
 * Screen showing details of a plagiarism report
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailScreen(
    reportId: Long,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToCodeComparison: (Long) -> Unit,
    viewModel: ReportDetailViewModel = hiltViewModel()
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
    val report = uiState.report
    val similarities = uiState.similarities
    var threshold by remember { mutableStateOf(0f) }
    val filteredSimilarities = remember(similarities, threshold) {
        if (threshold <= 0f) similarities else similarities.filter { it.similarityScore >= threshold }
    }
    val filteredHighSimilarities = remember(similarities, threshold) {
        similarities.filter { it.similarityScore >= threshold }
            .sortedByDescending { it.similarityScore }
    }

    LaunchedEffect(reportId) {
        viewModel.loadReport(reportId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("报告详情 #${reportId}") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateHome) {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "主页"
                        )
                    }
                }
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                LoadingIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.report != null) {
                val canView = currentUser?.role == com.example.codechecker.domain.model.Role.TEACHER ||
                        (currentUser?.role == com.example.codechecker.domain.model.Role.STUDENT && uiState.report?.executorId == currentUser?.id)
                if (!canView) {
                    Text(
                        text = "仅可查看本人报告或教师报告",
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        ReportSummaryCard(
                            report = report!!,
                            similarityCount = similarities.size,
                            latestStudentCount = uiState.latestStudentCount,
                            totalSubmissionCount = uiState.totalSubmissionCount
                        )
                    }

                    item {
                        Text(
                            text = "高相似度（≥${threshold.toInt()}%）",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    if (filteredHighSimilarities.isEmpty()) {
                        item {
                            Text(
                                text = "未发现相似度达到阈值的代码对",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(filteredHighSimilarities) { similarity ->
                            HighSimilarityCard(
                                similarity = similarity,
                                submissionsById = uiState.submissionsById,
                                onClick = { onNavigateToCodeComparison(similarity.id) },
                                onAnalyze = { viewModel.analyzeSimilarity(similarity.id) },
                                enabled = similarity.similarityScore >= uiState.similarityThreshold
                            )
                        }
                    }

                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "所有比对结果",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "筛选：相似度 ≥ ${threshold.toInt()}%",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Slider(
                                        value = threshold,
                                        onValueChange = { threshold = it },
                                        valueRange = 0f..100f,
                                        steps = 19
                                    )
                                }
                                OutlinedButton(onClick = { threshold = 0f }) {
                                    Text("重置")
                                }
                            }
                        }
                    }

                    items(filteredSimilarities) { similarity ->
                        SimilarityCard(
                            similarity = similarity,
                            submissionsById = uiState.submissionsById,
                            onClick = { onNavigateToCodeComparison(similarity.id) }
                        )
                    }
                }
                }
            } else {
                Text(
                    text = "报告不存在",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            AiResultDialog(
                uiState = uiState,
                onRetry = {
                    uiState.selectedSimilarityId?.let { viewModel.analyzeSimilarity(it) }
                    viewModel.clearAiError()
                },
                onCancel = {
                    viewModel.clearAiError()
                }
            )
        }
    }
}

@Composable
private fun ReportSummaryCard(
    report: com.example.codechecker.domain.model.Report,
    similarityCount: Int,
    latestStudentCount: Int,
    totalSubmissionCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "报告概要",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "总提交数",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${report.totalSubmissions}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Column {
                    Text(
                        text = "总对比数",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${report.totalPairs}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Column {
                    Text(
                        text = "结果数",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$similarityCount",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            val scopeText = when {
                report.totalSubmissions == latestStudentCount && report.totalPairs == latestStudentCount * (latestStudentCount - 1) / 2 -> "范围：仅最后一次提交"
                report.totalSubmissions == totalSubmissionCount && report.totalPairs == totalSubmissionCount * (totalSubmissionCount - 1) / 2 -> "范围：所有历史提交"
                report.totalSubmissions == latestStudentCount && report.totalPairs == latestStudentCount - 1 -> "范围：学生个人最新比对"
                else -> "范围：未知"
            }
            Text(
                text = scopeText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun HighSimilarityCard(
    similarity: Similarity,
    submissionsById: Map<Long, Submission>,
    onClick: () -> Unit,
    onAnalyze: () -> Unit,
    enabled: Boolean
) {
    @OptIn(ExperimentalMaterial3Api::class)
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                val s1 = submissionsById[similarity.submission1Id]
                val s2 = submissionsById[similarity.submission2Id]
                val left = s1?.let { "${it.studentName.ifBlank { "学生#${it.studentId}" }}(${it.fileName})" } ?: "提交 #${similarity.submission1Id}"
                val right = s2?.let { "${it.studentName.ifBlank { "学生#${it.studentId}" }}(${it.fileName})" } ?: "提交 #${similarity.submission2Id}"
                Text(
                    text = "$left ↔ $right",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "相似度: ${String.format("%.2f", similarity.similarityScore)}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "查看详情 →",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = onAnalyze, enabled = enabled) {
                    Text("AI分析")
                }
            }
        }
    }
}

@Composable
private fun AiResultDialog(
    uiState: ReportDetailUiState,
    onRetry: () -> Unit,
    onCancel: () -> Unit
) {
    val show = uiState.aiResult != null || uiState.aiError == "timeout" || (uiState.aiError != null)
    if (!show) return
    val title = when {
        uiState.aiError == "timeout" -> "网络超时"
        uiState.aiError != null -> "分析失败"
        else -> "AI分析结果"
    }
    val contentText = when {
        uiState.aiError == "timeout" -> "AI分析请求超时，是否重试？"
        uiState.aiError != null -> uiState.aiError ?: "分析失败"
        uiState.aiResult is com.example.codechecker.domain.model.AIAnalysisResult.Success -> {
            val s = uiState.aiResult as com.example.codechecker.domain.model.AIAnalysisResult.Success
            "风险:${s.plagiarismRisk}\n原因:${s.reason}\n分析:${s.analysis}"
        }
        uiState.aiResult is com.example.codechecker.domain.model.AIAnalysisResult.Error -> {
            val e = uiState.aiResult as com.example.codechecker.domain.model.AIAnalysisResult.Error
            "分析失败:${e.message}"
        }
        else -> ""
    }
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(title) },
        text = { Text(contentText) },
        confirmButton = {
            if (uiState.aiError == "timeout" && uiState.selectedSimilarityId != null) {
                TextButton(onClick = onCancel) { Text("取消") }
                TextButton(onClick = onRetry) { Text("重试") }
            } else {
                TextButton(onClick = onCancel) { Text("确定") }
            }
        }
    )
}

@Composable
private fun SimilarityCard(
    similarity: Similarity,
    submissionsById: Map<Long, Submission>,
    onClick: () -> Unit
) {
    @OptIn(ExperimentalMaterial3Api::class)
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                val s1 = submissionsById[similarity.submission1Id]
                val s2 = submissionsById[similarity.submission2Id]
                val left = s1?.let { "${it.studentName.ifBlank { "学生#${it.studentId}" }}(${it.fileName})" } ?: "提交 #${similarity.submission1Id}"
                val right = s2?.let { "${it.studentName.ifBlank { "学生#${it.studentId}" }}(${it.fileName})" } ?: "提交 #${similarity.submission2Id}"
                Text(
                    text = "$left ↔ $right",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "相似度: ${String.format("%.2f", similarity.similarityScore)}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "查看详情 →",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
