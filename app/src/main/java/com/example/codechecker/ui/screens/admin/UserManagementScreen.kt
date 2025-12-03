package com.example.codechecker.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codechecker.domain.model.Role
import com.example.codechecker.domain.model.User
import com.example.codechecker.domain.model.UserStatus
import com.example.codechecker.ui.screens.admin.viewmodel.UserManagementViewModel

/**
 * User Management Screen
 * Allows administrators to view, create, edit, delete, and manage user accounts
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    onNavigateBack: () -> Unit,
    viewModel: UserManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showResetPasswordDialog by remember { mutableStateOf(false) }
    var showRoleChangeDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var userToEdit by remember { mutableStateOf<User?>(null) }
    var userToDelete by remember { mutableStateOf<User?>(null) }
    var userToResetPassword by remember { mutableStateOf<User?>(null) }
    var userToChangeRole by remember { mutableStateOf<User?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("用户管理") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showImportDialog = true }) {
                        Icon(Icons.Default.Upload, contentDescription = "导入用户")
                    }
                    IconButton(onClick = { viewModel.exportUsers() }) {
                        Icon(Icons.Default.Download, contentDescription = "导出用户")
                    }
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "创建用户")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.searchUsers(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("搜索用户") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            // Users List
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                val errorMsg = uiState.error
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMsg ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else if (uiState.users.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("没有找到用户")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.users) { user ->
                        UserListItem(
                            user = user,
                            onEdit = {
                                userToEdit = user
                                showEditDialog = true
                            },
                            onDelete = {
                                userToDelete = user
                                showDeleteDialog = true
                            },
                            onDisable = { viewModel.disableUser(user.id, 0) }, // TODO: Get current admin ID
                            onEnable = { viewModel.enableUser(user.id, 0) }, // TODO: Get current admin ID
                            onChangeRole = {
                                userToChangeRole = user
                                showRoleChangeDialog = true
                            },
                            onResetPassword = {
                                userToResetPassword = user
                                showResetPasswordDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Create User Dialog
    if (showCreateDialog) {
        CreateUserDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { username, password, displayName, role ->
                viewModel.createUser(username, password, displayName, role, 0) // TODO: Get current admin ID
                showCreateDialog = false
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && userToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除用户 ${userToDelete!!.username} 吗？此操作无法撤销。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        userToDelete?.let { viewModel.deleteUser(it.id, 0) } // TODO: Get current admin ID
                        showDeleteDialog = false
                        userToDelete = null
                    }
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    userToDelete = null
                }) {
                    Text("取消")
                }
            }
        )
    }

    // Edit User Dialog
    if (showEditDialog && userToEdit != null) {
        EditUserDialog(
            user = userToEdit!!,
            onDismiss = {
                showEditDialog = false
                userToEdit = null
            },
            onUpdate = { displayName, newRole ->
                viewModel.updateUser(userToEdit!!.id, displayName, newRole, 0) // TODO: Get current admin ID
                showEditDialog = false
                userToEdit = null
            }
        )
    }

    // Reset Password Dialog
    if (showResetPasswordDialog && userToResetPassword != null) {
        AlertDialog(
            onDismissRequest = { showResetPasswordDialog = false },
            title = { Text("重置密码") },
            text = {
                Column {
                    Text(
                        text = "确定要重置用户 ${userToResetPassword!!.displayName} 的密码吗？",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "系统将自动生成一个随机密码。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetPassword(userToResetPassword!!.id, 0) // TODO: Get current admin ID
                        showResetPasswordDialog = false
                        userToResetPassword = null
                    }
                ) {
                    Text("确认重置")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showResetPasswordDialog = false
                    userToResetPassword = null
                }) {
                    Text("取消")
                }
            }
        )
    }

    // Role Change Dialog
    if (showRoleChangeDialog && userToChangeRole != null) {
        RoleChangeDialog(
            user = userToChangeRole!!,
            onDismiss = {
                showRoleChangeDialog = false
                userToChangeRole = null
            },
            onConfirm = { newRole ->
                viewModel.changeUserRole(userToChangeRole!!.id, newRole, 0) // TODO: Get current admin ID
                showRoleChangeDialog = false
                userToChangeRole = null
            }
        )
    }

    // Import Users Dialog
    if (showImportDialog) {
        ImportUsersDialog(
            onDismiss = { showImportDialog = false },
            onImport = { format ->
                viewModel.importUsers(format)
                showImportDialog = false
            }
        )
    }
}

@Composable
fun UserListItem(
    user: User,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDisable: () -> Unit,
    onEnable: () -> Unit,
    onChangeRole: (Role) -> Unit,
    onResetPassword: () -> Unit
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = user.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "@${user.username}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status Badge
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                text = if (user.status == UserStatus.ACTIVE) "活跃" else "已禁用",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (user.status == UserStatus.ACTIVE) Icons.Default.CheckCircle else Icons.Default.Block,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )

                    // Role Badge
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                text = when (user.role) {
                                    Role.ADMIN -> "管理员"
                                    Role.TEACHER -> "教师"
                                    Role.STUDENT -> "学生"
                                },
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = when (user.role) {
                                    Role.ADMIN -> Icons.Default.AdminPanelSettings
                                    Role.TEACHER -> Icons.Default.School
                                    Role.STUDENT -> Icons.Default.Person
                                },
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // First Row: Edit, Change Role, Reset Password
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("编辑")
                    }

                    OutlinedButton(
                        onClick = { onChangeRole(user.role) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("改角色")
                    }

                    OutlinedButton(
                        onClick = onResetPassword,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.LockReset, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("重置密码")
                    }
                }

                // Second Row: Enable/Disable and Delete
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Enable/Disable Button
                    if (user.status == UserStatus.ACTIVE) {
                        OutlinedButton(
                            onClick = onDisable,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("禁用")
                        }
                    } else {
                        OutlinedButton(
                            onClick = onEnable,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("启用")
                        }
                    }

                    // Delete Button
                    OutlinedButton(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("删除")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUserDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String, Role) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(Role.STUDENT) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("创建用户") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("用户名") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("密码") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("显示名称") },
                    singleLine = true
                )
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = when (selectedRole) {
                            Role.ADMIN -> "管理员"
                            Role.TEACHER -> "教师"
                            Role.STUDENT -> "学生"
                        },
                        onValueChange = { },
                        label = { Text("角色") },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("学生") },
                            onClick = {
                                selectedRole = Role.STUDENT
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("教师") },
                            onClick = {
                                selectedRole = Role.TEACHER
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("管理员") },
                            onClick = {
                                selectedRole = Role.ADMIN
                                expanded = false
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onCreate(username, password, displayName, selectedRole)
                },
                enabled = username.isNotBlank() && password.isNotBlank() && displayName.isNotBlank()
            ) {
                Text("创建")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserDialog(
    user: User,
    onDismiss: () -> Unit,
    onUpdate: (String, Role) -> Unit
) {
    var displayName by remember { mutableStateOf(user.displayName) }
    var selectedRole by remember { mutableStateOf(user.role) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("编辑用户") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "用户名: ${user.username}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("显示名称") },
                    singleLine = true
                )
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = when (selectedRole) {
                            Role.ADMIN -> "管理员"
                            Role.TEACHER -> "教师"
                            Role.STUDENT -> "学生"
                        },
                        onValueChange = { },
                        label = { Text("角色") },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("学生") },
                            onClick = {
                                selectedRole = Role.STUDENT
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("教师") },
                            onClick = {
                                selectedRole = Role.TEACHER
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("管理员") },
                            onClick = {
                                selectedRole = Role.ADMIN
                                expanded = false
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onUpdate(displayName, selectedRole)
                },
                enabled = displayName.isNotBlank()
            ) {
                Text("更新")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleChangeDialog(
    user: User,
    onDismiss: () -> Unit,
    onConfirm: (Role) -> Unit
) {
    var selectedRole by remember { mutableStateOf(user.role) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("更改角色") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "用户: ${user.displayName}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "当前角色: ${
                        when (user.role) {
                            Role.ADMIN -> "管理员"
                            Role.TEACHER -> "教师"
                            Role.STUDENT -> "学生"
                        }
                    }",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "选择新角色:",
                    style = MaterialTheme.typography.bodyMedium
                )
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = when (selectedRole) {
                            Role.ADMIN -> "管理员"
                            Role.TEACHER -> "教师"
                            Role.STUDENT -> "学生"
                        },
                        onValueChange = { },
                        label = { Text("新角色") },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("学生") },
                            onClick = {
                                selectedRole = Role.STUDENT
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("教师") },
                            onClick = {
                                selectedRole = Role.TEACHER
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("管理员") },
                            onClick = {
                                selectedRole = Role.ADMIN
                                expanded = false
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(selectedRole)
                },
                enabled = selectedRole != user.role
            ) {
                Text("确认更改")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportUsersDialog(
    onDismiss: () -> Unit,
    onImport: (String) -> Unit
) {
    var selectedFormat by remember { mutableStateOf("JSON") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("导入用户") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "从文件导入用户数据",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "选择导入格式:",
                    style = MaterialTheme.typography.bodySmall
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("JSON", "CSV").forEach { format ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedFormat == format,
                                onClick = { selectedFormat = format }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(format)
                        }
                    }
                }
                Text(
                    text = "导入文件应包含用户的必要信息，系统将自动验证和创建用户。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onImport(selectedFormat)
                }
            ) {
                Text("开始导入")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
