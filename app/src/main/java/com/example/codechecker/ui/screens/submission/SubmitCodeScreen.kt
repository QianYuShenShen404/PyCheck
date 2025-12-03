package com.example.codechecker.ui.screens.submission

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codechecker.ui.components.LoadingIndicator

/**
 * Screen for submitting Python code files
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitCodeScreen(
    assignmentId: Long,
    onNavigateBack: () -> Unit,
    onSubmissionSuccess: () -> Unit,
    onNavigateHome: () -> Unit,
    viewModel: SubmitCodeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var codeText by remember { mutableStateOf("") }

    val context = androidx.compose.ui.platform.LocalContext.current
    val userSessionManager = remember {
        dagger.hilt.android.EntryPointAccessors.fromApplication(
            context.applicationContext,
            com.example.codechecker.di.UtilityModuleEntryPoint::class.java
        ).userSessionManager()
    }
    val currentUser by userSessionManager.currentUser.collectAsStateWithLifecycle(initialValue = null)

    LaunchedEffect(uiState.isSubmissionSuccess) {
        if (uiState.isSubmissionSuccess) {
            snackbarHostState.showSnackbar("提交成功")
            onSubmissionSuccess()
        }
    }

    fun handleSubmit() {
        if (codeText.isNotBlank()) {
            viewModel.submitCodeText(
                assignmentId = assignmentId,
                codeContent = codeText
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("提交代码") },
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Instructions
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
                            text = "提交说明",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "请在下方输入或粘贴Python代码进行提交。",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    val lines = remember(codeText) { codeText.lines() }
                    Column(
                        modifier = Modifier
                            .width(48.dp)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Top
                    ) {
                        lines.forEachIndexed { index, _ ->
                            Text(
                                text = (index + 1).toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    OutlinedTextField(
                        value = codeText,
                        onValueChange = { codeText = it },
                        label = { Text("Python代码") },
                        placeholder = { Text("在此输入或粘贴代码…") },
                        modifier = Modifier
                            .fillMaxSize(),
                        maxLines = Int.MAX_VALUE,
                        enabled = currentUser?.role == com.example.codechecker.domain.model.Role.STUDENT
                    )
                }

                // Submit button
                Button(
                    onClick = { handleSubmit() },
                    enabled = codeText.isNotBlank() && !uiState.isLoading && currentUser?.role == com.example.codechecker.domain.model.Role.STUDENT,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("提交代码")
                }

                uiState.error?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (currentUser?.role != com.example.codechecker.domain.model.Role.STUDENT) {
                    Text(
                        text = "仅学生可提交代码",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

            }
        }
    }
}
