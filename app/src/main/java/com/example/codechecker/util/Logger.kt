package com.example.codechecker.util

import android.util.Log
import com.example.codechecker.domain.model.AdminSettings
import com.example.codechecker.domain.model.LogLevel
import com.example.codechecker.domain.repository.AdminSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Logger utility for the application
 * Provides centralized logging with different levels
 */
@Singleton
class Logger @Inject constructor(
    private val settingsRepository: AdminSettingsRepository
) {

    companion object {
        private const val TAG_PREFIX = "CodeChecker"
    }

    enum class Level(val priority: Int, val tag: String) {
        VERBOSE(Log.VERBOSE, "V"),
        DEBUG(Log.DEBUG, "D"),
        INFO(Log.INFO, "I"),
        WARNING(Log.WARN, "W"),
        ERROR(Log.ERROR, "E")
    }

    private var currentLevel: Level = Level.INFO

    init {
        loadLogLevel()
    }

    private fun loadLogLevel() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val settings = settingsRepository.getAdminSettings()
                val logLevel = settings.logLevel
                currentLevel = when (logLevel) {
                    LogLevel.DEBUG -> Level.DEBUG
                    LogLevel.INFO -> Level.INFO
                    LogLevel.WARN -> Level.WARNING
                    LogLevel.ERROR -> Level.ERROR
                }
            } catch (e: Exception) {
                Log.e(TAG_PREFIX, "Failed to load log level setting", e)
            }
        }
    }

    fun verbose(message: String, throwable: Throwable? = null) {
        if (currentLevel.priority <= Level.VERBOSE.priority) {
            log(Level.VERBOSE, message, throwable)
        }
    }

    fun debug(message: String, throwable: Throwable? = null) {
        if (currentLevel.priority <= Level.DEBUG.priority) {
            log(Level.DEBUG, message, throwable)
        }
    }

    fun info(message: String, throwable: Throwable? = null) {
        if (currentLevel.priority <= Level.INFO.priority) {
            log(Level.INFO, message, throwable)
        }
    }

    fun warning(message: String, throwable: Throwable? = null) {
        if (currentLevel.priority <= Level.WARNING.priority) {
            log(Level.WARNING, message, throwable)
        }
    }

    fun error(message: String, throwable: Throwable? = null) {
        if (currentLevel.priority <= Level.ERROR.priority) {
            log(Level.ERROR, message, throwable)
        }
    }

    fun log(level: Level, message: String, throwable: Throwable? = null) {
        val tag = "$TAG_PREFIX:${level.tag}"
        if (throwable != null) {
            Log.println(level.priority, tag, "$message\n${Log.getStackTraceString(throwable)}")
        } else {
            Log.println(level.priority, tag, message)
        }
    }

    fun userAction(action: String) {
        info("USER_ACTION: $action")
    }

    fun plagiarismResult(assignmentId: Long, reportId: Long?, similarityCount: Int, highSimilarityCount: Int) {
        info("PLAGIARISM_RESULT: assignmentId=$assignmentId, reportId=$reportId, " +
                "totalComparisons=$similarityCount, highSimilarity=$highSimilarityCount")
    }

    fun errorReport(error: String, context: String, throwable: Throwable? = null) {
        error("ERROR_REPORT [$context]: $error", throwable)
    }

    fun setLogLevel(level: Level) {
        currentLevel = level
    }
}
