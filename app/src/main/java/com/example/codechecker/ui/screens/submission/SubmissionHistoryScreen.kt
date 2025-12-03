package com.example.codechecker.ui.screens.submission

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
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
import com.example.codechecker.domain.model.Submission
import com.example.codechecker.ui.components.EmptyState
import com.example.codechecker.ui.components.LoadingIndicator
import com.example.codechecker.di.UtilityModuleEntryPoint
import com.example.codechecker.util.TimeUtils
import dagger.hilt.android.EntryPointAccessors

/**
 * Screen showing submission history for a student
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmissionHistoryScreen(
    assignmentId: Long? = null,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToSubmissionDetail: (Long) -> Unit,
    viewModel: SubmissionHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val submissions = uiState.submissions
    val context = LocalContext.current
    val timeUtils = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            UtilityModuleEntryPoint::class.java
        ).timeUtils()
    }

    LaunchedEffect(assignmentId) {
        if (assignmentId != null) {
            viewModel.loadSubmissions(assignmentId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("提交历史") },
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
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                LoadingIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (submissions.isEmpty()) {
                EmptyState(
                    title = "暂无提交记录",
                    message = "您还没有提交过任何作业",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "我的提交",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(submissions) { submission ->
                        SubmissionCard(
                            submission = submission,
                            timeUtils = timeUtils,
                            onViewClick = { onNavigateToSubmissionDetail(submission.id) }
                        )
                    }
                }
            }

            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("重试")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
}

/**
 * Card component for displaying submission information
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubmissionCard(
    submission: Submission,
    timeUtils: TimeUtils,
    onViewClick: () -> Unit
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = submission.fileName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "作业ID: ${submission.assignmentId}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "提交时间: ${timeUtils.formatDateTime(submission.submittedAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "哈希: ${submission.codeHash.take(16)}...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Status badge
                Surface(
                    color = when (submission.status) {
                        com.example.codechecker.domain.model.SubmissionStatus.SUBMITTED -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        com.example.codechecker.domain.model.SubmissionStatus.ANALYZED -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                        com.example.codechecker.domain.model.SubmissionStatus.PROCESSED -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    val statusText = when (submission.status) {
                        com.example.codechecker.domain.model.SubmissionStatus.SUBMITTED -> "已提交"
                        com.example.codechecker.domain.model.SubmissionStatus.ANALYZED -> "已分析"
                        com.example.codechecker.domain.model.SubmissionStatus.PROCESSED -> "已处理"
                    }
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelMedium,
                        color = when (submission.status) {
                            com.example.codechecker.domain.model.SubmissionStatus.SUBMITTED -> MaterialTheme.colorScheme.primary
                            com.example.codechecker.domain.model.SubmissionStatus.ANALYZED -> MaterialTheme.colorScheme.tertiary
                            com.example.codechecker.domain.model.SubmissionStatus.PROCESSED -> MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onViewClick) {
                    Text("查看")
                }
            }
        }
    }
}
