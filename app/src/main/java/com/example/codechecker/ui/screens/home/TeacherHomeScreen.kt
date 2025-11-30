package com.example.codechecker.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Logout
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
import com.example.codechecker.ui.components.EmptyState
import com.example.codechecker.ui.components.LoadingIndicator
import com.example.codechecker.di.UtilityModuleEntryPoint
import com.example.codechecker.util.TimeUtils
import dagger.hilt.android.EntryPointAccessors

/**
 * Home screen for teacher users
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherHomeScreen(
    onNavigateToCreateAssignment: () -> Unit,
    onNavigateToAssignmentList: () -> Unit,
    onNavigateToAssignmentDetail: (Long) -> Unit,
    onLogout: () -> Unit,
    viewModel: TeacherHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle(initialValue = null)
    val context = LocalContext.current
    val timeUtils = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            UtilityModuleEntryPoint::class.java
        ).timeUtils()
    }

    val assignments = uiState.assignments

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "欢迎，${currentUser?.displayName ?: ""}（教师）",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "退出登录"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateAssignment,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "创建作业"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                LoadingIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (assignments.isEmpty()) {
                EmptyState(
                    title = "暂无作业",
                    message = "点击右下角按钮创建第一个作业",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "我的作业",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(onClick = onNavigateToAssignmentList) {
                                Text("查看全部")
                            }
                        }
                    }
                    items(assignments.take(5)) { assignment ->
                        AssignmentCard(
                            assignment = assignment,
                            timeUtils = timeUtils,
                            onClick = { onNavigateToAssignmentDetail(assignment.id) }
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
 * Card component for displaying assignment information
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AssignmentCard(
    assignment: Assignment,
    timeUtils: TimeUtils,
    onClick: () -> Unit
) {
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
            Text(
                text = assignment.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (!assignment.description.isNullOrEmpty()) {
                Text(
                    text = assignment.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            val dueDateText = assignment.dueDate?.let { dueDate ->
                if (dueDate > 0) {
                    "截止时间：${timeUtils.formatDateTime(dueDate)}"
                } else {
                    "无截止时间"
                }
            } ?: "无截止时间"
            Text(
                text = dueDateText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Assignment status
            val statusColor = assignment.dueDate?.let { dueDate ->
                when {
                    dueDate > 0 && dueDate < System.currentTimeMillis() ->
                        MaterialTheme.colorScheme.error
                    dueDate > 0 && dueDate - System.currentTimeMillis() < 24 * 60 * 60 * 1000L ->
                        MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.primary
                }
            } ?: MaterialTheme.colorScheme.primary
            val statusText = assignment.dueDate?.let { dueDate ->
                when {
                    dueDate > 0 && dueDate < System.currentTimeMillis() -> "已截止"
                    dueDate > 0 && dueDate - System.currentTimeMillis() < 24 * 60 * 60 * 1000L -> "即将截止"
                    else -> "进行中"
                }
            } ?: "进行中"

            Surface(
                color = statusColor.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
