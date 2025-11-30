package com.example.codechecker.ui.screens.assignment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codechecker.R
import com.example.codechecker.domain.model.Assignment
import com.example.codechecker.ui.components.LoadingIndicator
import com.example.codechecker.ui.navigation.createSubmissionRoute
import com.example.codechecker.di.UtilityModuleEntryPoint
import com.example.codechecker.util.TimeUtils
import dagger.hilt.android.EntryPointAccessors

/**
 * Detail screen for a specific assignment
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentDetailScreen(
    assignmentId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToSubmission: (Long) -> Unit,
    onNavigateToSubmissionList: (Long) -> Unit,
    onNavigateToReportList: (Long) -> Unit,
    onNavigateToReportDetail: (Long) -> Unit,
    viewModel: AssignmentDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val assignment = uiState.assignment
    val context = LocalContext.current
    val timeUtils = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            UtilityModuleEntryPoint::class.java
        ).timeUtils()
    }
    val userSessionManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            UtilityModuleEntryPoint::class.java
        ).userSessionManager()
    }
    val currentUser by userSessionManager.currentUser.collectAsStateWithLifecycle(initialValue = null)

    LaunchedEffect(assignmentId) {
        viewModel.loadAssignment(assignmentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("作业详情") },
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
            } else if (assignment != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AssignmentInfoCard(assignment = assignment, timeUtils = timeUtils)

                    // Submission status card
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
                                text = "提交状态",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            if (uiState.submissionCount > 0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "已提交 ${uiState.submissionCount} 个文件",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    TextButton(
                                        onClick = { onNavigateToSubmissionList(assignmentId) }
                                    ) {
                                        Text("查看提交")
                                    }
                                }
                            } else {
                                Text(
                                    text = "尚未提交",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    if (currentUser?.role == com.example.codechecker.domain.model.Role.STUDENT) {
                        Button(
                            onClick = { onNavigateToSubmission(assignmentId) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.UploadFile,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(if (uiState.submissionCount > 0) "继续提交" else "提交代码")
                        }
                    }

                    // Plagiarism report entry
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
                                text = "查重报告",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "查看本作业的查重报告列表并进入详情页",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            if (currentUser?.role == com.example.codechecker.domain.model.Role.TEACHER) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { onNavigateToReportList(assignmentId) }) {
                                        Text("查看报告")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { viewModel.generateReportLatestOnly(assignmentId) },
                                        enabled = !uiState.isGeneratingReport
                                    ) {
                                        if (uiState.isGeneratingReport) {
                                            CircularProgressIndicator(
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                        }
                                        Text("生成（仅最后一次提交）")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    OutlinedButton(
                                        onClick = { viewModel.generateReportAllHistory(assignmentId) },
                                        enabled = !uiState.isGeneratingReport
                                    ) {
                                        Text("生成（包含所有历史提交）")
                                    }
                                }
                            }
                            if (currentUser?.role == com.example.codechecker.domain.model.Role.STUDENT) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { onNavigateToReportList(assignmentId) }) {
                                        Text("查看报告")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { viewModel.generateStudentLatestReport(assignmentId) },
                                        enabled = !uiState.isGeneratingReport
                                    ) {
                                        if (uiState.isGeneratingReport) {
                                            CircularProgressIndicator(
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                        }
                                        Text("生成我的查重报告")
                                    }
                                }
                            }
                            if (uiState.generateError != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = uiState.generateError!!,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            if (uiState.generatedReportId != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                AssistChip(
                                    onClick = { onNavigateToReportDetail(uiState.generatedReportId!!) },
                                    label = { Text("查看刚生成的报告 #${uiState.generatedReportId}") }
                                )
                            }
                        }
                    }

                    uiState.error?.let { error ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            OutlinedButton(onClick = { viewModel.loadAssignment(assignmentId) }) {
                                Text("重试")
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "作业不存在",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

/**
 * Card component for displaying assignment information
 */
@Composable
private fun AssignmentInfoCard(assignment: Assignment, timeUtils: TimeUtils) {
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
                text = assignment.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (assignment.description.isNotEmpty()) {
                Text(
                    text = "作业描述",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = assignment.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Due date
            Text(
                text = "截止时间",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            val dueDateText = assignment.dueDate?.let { dueDate ->
                if (dueDate > 0) {
                    timeUtils.formatDateTime(dueDate)
                } else {
                    "无截止时间"
                }
            } ?: "无截止时间"
            Text(
                text = dueDateText,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Submission limit
            Text(
                text = "提交限制",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            val limitText = when (assignment.submissionLimit) {
                com.example.codechecker.domain.model.SubmissionLimit.SMALL -> "小型作业（最多200份）"
                com.example.codechecker.domain.model.SubmissionLimit.LARGE -> "大型作业（最多500份）"
                com.example.codechecker.domain.model.SubmissionLimit.UNLIMITED -> "无限制"
            }
            Text(
                text = limitText,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Python version
            Text(
                text = "Python版本",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            val pythonVersionText = when (assignment.pythonVersion) {
                com.example.codechecker.domain.model.PythonVersion.PYTHON2 -> "Python 2.x"
                com.example.codechecker.domain.model.PythonVersion.PYTHON3 -> "Python 3.x"
                com.example.codechecker.domain.model.PythonVersion.COMPATIBLE -> "兼容所有版本"
            }
            Text(
                text = pythonVersionText,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
