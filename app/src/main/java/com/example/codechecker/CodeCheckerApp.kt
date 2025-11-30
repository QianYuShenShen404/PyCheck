package com.example.codechecker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for CodeChecker
 * Initializes Hilt dependency injection
 */
@HiltAndroidApp
class CodeCheckerApp : Application()
