package com.example.codechecker.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codechecker.data.preference.UserSessionManager
import com.example.codechecker.domain.model.Role
import dagger.hilt.android.EntryPointAccessors
import androidx.compose.ui.platform.LocalContext

/**
 * Main screen container with navigation and authentication state handling
 */
@Composable
fun MainScreen(
    startDestination: String = Screen.SPLASH
) {
    val context = LocalContext.current
    val userSessionManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            com.example.codechecker.di.UtilityModuleEntryPoint::class.java
        ).userSessionManager()
    }
    val currentUser by userSessionManager.currentUser.collectAsStateWithLifecycle(initialValue = null)

    val actualStartDestination = startDestination

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavGraph(startDestination = actualStartDestination)
    }
}
