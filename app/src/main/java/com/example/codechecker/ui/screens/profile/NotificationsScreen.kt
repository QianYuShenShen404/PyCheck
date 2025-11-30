package com.example.codechecker.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NotificationsScreen(onNavigateBack: () -> Unit) {
    var enableReminders by remember { mutableStateOf(true) }
    var enableResults by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("通知设置")
        Text("接收作业提醒")
        Switch(checked = enableReminders, onCheckedChange = { enableReminders = it })
        Text("接收查重结果通知")
        Switch(checked = enableResults, onCheckedChange = { enableResults = it })
    }
}

