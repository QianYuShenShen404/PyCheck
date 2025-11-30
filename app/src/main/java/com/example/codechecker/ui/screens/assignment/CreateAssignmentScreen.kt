package com.example.codechecker.ui.screens.assignment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codechecker.R
import com.example.codechecker.ui.components.LoadingIndicator

/**
 * Screen for creating and editing assignments
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAssignmentScreen(
    onNavigateBack: () -> Unit,
    onAssignmentCreated: (Long) -> Unit,
    viewModel: CreateAssignmentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDateText by remember { mutableStateOf("") }
    var selectedLimitIndex by remember { mutableStateOf(0) }
    var selectedPythonVersionIndex by remember { mutableStateOf(2) }

    val submissionLimitOptions = listOf(
        "小型作业（最多200份）" to 200,
        "大型作业（最多500份）" to 500,
        "无限制" to 0
    )

    val pythonVersionOptions = listOf(
        "Python 2.x" to "PYTHON2",
        "Python 3.x" to "PYTHON3",
        "兼容所有版本" to "COMPATIBLE"
    )

    val dueDateError by viewModel.dueDateError.collectAsStateWithLifecycle()
    val titleError by viewModel.titleError.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            uiState.createdAssignmentId?.let { assignmentId ->
                onAssignmentCreated(assignmentId)
            }
        }
    }

    fun handleCreate() {
        viewModel.createAssignment(
            title = title,
            description = description,
            dueDateText = dueDateText,
            submissionLimit = submissionLimitOptions[selectedLimitIndex].second,
            pythonVersion = pythonVersionOptions[selectedPythonVersionIndex].second
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("创建作业") },
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
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("作业标题 *") },
                    placeholder = { Text("请输入作业标题") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
                    ),
                    isError = titleError != null,
                    supportingText = {
                        titleError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Description field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("作业描述") },
                    placeholder = { Text("请输入作业描述") },
                    minLines = 4,
                    maxLines = 6,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Due date field
                OutlinedTextField(
                    value = dueDateText,
                    onValueChange = { dueDateText = it },
                    label = { Text("截止日期") },
                    placeholder = { Text("格式: yyyy-MM-dd HH:mm (可选)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
                    ),
                    isError = dueDateError != null,
                    supportingText = {
                        Column {
                            Text("留空表示无截止日期")
                            dueDateError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Submission limit selection
                Text(
                    text = "提交限制",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                submissionLimitOptions.forEachIndexed { index, (label, _) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedLimitIndex == index,
                            onClick = { selectedLimitIndex = index }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(label)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Python version selection
                Text(
                    text = "Python版本",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                pythonVersionOptions.forEachIndexed { index, (label, _) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPythonVersionIndex == index,
                            onClick = { selectedPythonVersionIndex = index }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(label)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Create button
                Button(
                    onClick = { handleCreate() },
                    enabled = !uiState.isLoading && title.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("创建作业")
                }

                uiState.error?.let { error ->
                    Spacer(modifier = Modifier.height(16.dp))
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
