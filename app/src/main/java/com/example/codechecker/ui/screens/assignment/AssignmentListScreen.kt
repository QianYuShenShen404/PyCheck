package com.example.codechecker.ui.screens.assignment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Sort
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
import com.example.codechecker.domain.model.Assignment
import com.example.codechecker.domain.model.AssignmentStatus
import com.example.codechecker.ui.components.EmptyState
import com.example.codechecker.ui.components.LoadingIndicator
import com.example.codechecker.di.UtilityModuleEntryPoint
import com.example.codechecker.util.TimeUtils
import dagger.hilt.android.EntryPointAccessors

/**
 * Screen showing all assignments created by the teacher
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentListScreen(
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToAssignmentDetail: (Long) -> Unit,
    viewModel: AssignmentListViewModel = hiltViewModel()
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
    var selectedFilter by remember { mutableStateOf(0) }

    val filteredAssignments = remember(uiState.assignments, searchQuery, selectedFilter) {
        var filtered = uiState.assignments

        // Filter by status
        if (selectedFilter > 0) {
            val filterStatus = when (selectedFilter) {
                1 -> AssignmentStatus.ACTIVE
                2 -> AssignmentStatus.CLOSED
                else -> null
            }
            filterStatus?.let { filtered = filtered.filter { it.status == filterStatus } }
        }

        // Search
        if (searchQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
            }
        }

        filtered
    }

    val statusFilters = listOf("全部", "进行中", "已截止")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的作业") },
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
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "筛选排序"
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        statusFilters.forEachIndexed { index, label ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    selectedFilter = index
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
                        label = { Text("搜索作业") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Assignment count
                    Text(
                        text = "共 ${filteredAssignments.size} 个作业",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Assignments list
                    if (filteredAssignments.isEmpty()) {
                        EmptyState(
                            title = if (uiState.assignments.isEmpty()) "暂无作业" else "未找到匹配结果",
                            message = if (uiState.assignments.isEmpty())
                                "点击创建按钮开始创建第一个作业"
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
                                filteredAssignments,
                                key = { it.id }
                            ) { assignment ->
                                AssignmentCard(
                                    assignment = assignment,
                                    timeUtils = timeUtils,
                                    onClick = { onNavigateToAssignmentDetail(assignment.id) }
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = assignment.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                val statusText = when (assignment.status) {
                    AssignmentStatus.ACTIVE -> "进行中"
                    AssignmentStatus.CLOSED -> "已截止"
                    AssignmentStatus.DRAFT -> "草稿"
                }
                val statusColor = when (assignment.status) {
                    AssignmentStatus.ACTIVE -> MaterialTheme.colorScheme.primary
                    AssignmentStatus.CLOSED -> MaterialTheme.colorScheme.outline
                    AssignmentStatus.DRAFT -> MaterialTheme.colorScheme.tertiary
                }
                AssistChip(
                    onClick = { },
                    label = {
                        Text(
                            text = statusText,
                            color = statusColor
                        )
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (assignment.description.isNotEmpty()) {
                Text(
                    text = assignment.description.take(100) + if (assignment.description.length > 100) "..." else "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
        }
    }
}
