package com.example.codechecker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Application class for CodeChecker
 * Initializes Hilt dependency injection
 */
@HiltAndroidApp
class CodeCheckerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val entry = EntryPointAccessors.fromApplication(
            applicationContext,
            com.example.codechecker.di.AppEntryPoint::class.java
        )
        val useCase = entry.databaseValidationUseCase()
        val ensureAdmin = entry.ensureAdminExistsUseCase()
        CoroutineScope(Dispatchers.Default).launch {
            useCase.validateOnce()
            ensureAdmin.ensure()
        }
    }
}
