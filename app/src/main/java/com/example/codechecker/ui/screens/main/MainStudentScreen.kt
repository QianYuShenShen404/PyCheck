package com.example.codechecker.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.codechecker.ui.screens.home.StudentHomeScreen
import com.example.codechecker.ui.screens.profile.ProfileScreen

@Composable
fun MainStudentScreen(
    onNavigateToSubmissionHistory: () -> Unit,
    onNavigateToAssignmentDetail: (Long) -> Unit,
    onLogout: () -> Unit,
    onSwitchAccount: () -> Unit,
    onNavigateToAccount: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    onNavigateToGuide: () -> Unit
) {
    val selectedIndex = remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedIndex.value == 0,
                    onClick = { selectedIndex.value = 0 },
                    icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                    label = null
                )
                NavigationBarItem(
                    selected = selectedIndex.value == 1,
                    onClick = { selectedIndex.value = 1 },
                    icon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    label = null
                )
            }
        }
    ) { paddingValues ->
        Box(Modifier.fillMaxSize().padding(paddingValues)) {
            if (selectedIndex.value == 0) {
                StudentHomeScreen(
                    onNavigateToSubmissionHistory = onNavigateToSubmissionHistory,
                    onNavigateToAssignmentDetail = onNavigateToAssignmentDetail,
                    onLogout = onLogout
                )
            } else {
                ProfileScreen(
                    modifier = Modifier.align(Alignment.TopStart),
                    onLogout = onLogout,
                    onSwitchAccount = onSwitchAccount,
                    onNavigateToAccount = onNavigateToAccount,
                    onNavigateToNotifications = onNavigateToNotifications,
                    onNavigateToSecurity = onNavigateToSecurity,
                    onNavigateToGuide = onNavigateToGuide
                )
            }
        }
    }
}
