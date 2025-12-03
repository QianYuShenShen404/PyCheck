package com.example.codechecker.ui.screens.assignment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Home
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
import com.example.codechecker.domain.model.Submission
import com.example.codechecker.ui.components.EmptyState
import com.example.codechecker.ui.components.LoadingIndicator
import com.example.codechecker.di.UtilityModuleEntryPoint
import com.example.codechecker.util.TimeUtils
import dagger.hilt.android.EntryPointAccessors

/**
 * Screen showing all student submissions for an assignment
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmissionListScreen(
    assignmentId: Long,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToSubmissionDetail: (Long) -> Unit,
    viewModel: SubmissionListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    val timeUtils = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            UtilityModuleEntryPoint::class.java
        ).timeUtils()
    }

    var searchQuery by remember { mutableStateOf("") }
    var sortOrder by remember { mutableStateOf(0) }

    val filteredAndSortedSubmissions = remember(
        uiState.submissions,
        searchQuery,
        sortOrder
    ) {
        var filtered = uiState.submissions

        // Search by student name or file name
        if (searchQuery.isNotBlank()) {
            filtered = filtered.filter { submission ->
                submission.studentName.contains(searchQuery, ignoreCase = true) ||
                submission.fileName.contains(searchQuery, ignoreCase = true)
            }
        }

        // Sort
        when (sortOrder) {
            0 -> filtered.sortedByDescending { it.submittedAt } // Latest first
            1 -> filtered.sortedBy { it.submittedAt } // Earliest first
            2 -> filtered.sortedBy { it.studentName } // Student name
        }

        filtered
    }

    val sortOptions = listOf("最新提交", "最早提交", "学生姓名")

    LaunchedEffect(assignmentId) {
        viewModel.loadSubmissions(assignmentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.assignmentTitle) },
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
                    var expanded by remember { mutableStateOf(false) }
                    TextButton(onClick = { expanded = true }) {
                        Text("排序")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        sortOptions.forEachIndexed { index, label ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    sortOrder = index
                                    expanded = false
                                }
                            )
                        }
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
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Search bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("搜索学生或文件名") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Summary
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "提交统计",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${filteredAndSortedSubmissions.size}",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "总提交",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    val uniqueStudents = filteredAndSortedSubmissions
                                        .map { it.studentId }
                                        .distinct().size
                                    Text(
                                        text = "$uniqueStudents",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Text(
                                        text = "学生数",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${uiState.dueDate?.let { timeUtils.formatDateTime(it) } ?: "无"}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "截止日期",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    // Submissions list
                    if (filteredAndSortedSubmissions.isEmpty()) {
                        EmptyState(
                            title = if (uiState.submissions.isEmpty()) "暂无提交" else "未找到匹配结果",
                            message = if (uiState.submissions.isEmpty())
                                "等待学生提交作业"
                            else
                                "尝试修改搜索条件",
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                filteredAndSortedSubmissions,
                                key = { it.id }
                            ) { submission ->
                                SubmissionCard(
                                    submission = submission,
                                    timeUtils = timeUtils,
                                    onViewClick = { onNavigateToSubmissionDetail(submission.id) }
                                )
                            }
                        }
                    }

                    uiState.error?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

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
                        text = submission.studentName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = submission.fileName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                AssistChip(
                    onClick = { },
                    label = { Text("已提交") }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "提交时间: ${timeUtils.formatDateTime(submission.submittedAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "文件大小: ${submission.fileSize} bytes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "哈希: ${submission.codeHash.take(16)}...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "学生学号: ${submission.studentNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = onViewClick) {
                        Text("查看")
                    }
                }
        }
    }
}
