package com.example.codechecker.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Global error handler for the application
 * Provides centralized error handling and reporting
 */
@Singleton
class GlobalErrorHandler @Inject constructor(
    private val logger: Logger,
    private val context: Context
) {

    /**
     * Create a coroutine exception handler
     */
    fun createExceptionHandler(): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, exception ->
            handleError(exception, "CoroutineExecution")
        }
    }

    /**
     * Handle error with context
     */
    fun handleError(error: Throwable, context: String, userMessage: String? = null) {
        val errorId = generateErrorId()
        val errorMessage = error.message ?: "Unknown error"

        logger.errorReport(errorMessage, context, error)

        val shouldShowToUser = shouldShowToUser(error)
        val userFriendlyMessage = userMessage ?: getUserFriendlyMessage(error, context)

        if (shouldShowToUser) {
            showErrorToUser(userFriendlyMessage, errorId)
        }

        logErrorDetails(error, context, errorId)
    }

    /**
     * Determine if error should be shown to user
     */
    private fun shouldShowToUser(error: Throwable): Boolean {
        return when (error) {
            is SecurityException -> false
            is IllegalArgumentException -> true
            is IllegalStateException -> true
            else -> true
        }
    }

    /**
     * Get user-friendly error message
     */
    private fun getUserFriendlyMessage(error: Throwable, context: String): String {
        return when (context) {
            "Login" -> when (error) {
                is IllegalArgumentException -> "用户名或密码错误，请重试"
                else -> "登录失败，请检查网络连接后重试"
            }
            "Registration" -> when (error) {
                is IllegalArgumentException -> "注册信息无效：${error.message}"
                else -> "注册失败，请稍后重试"
            }
            "FileUpload" -> "文件上传失败，请检查文件格式和大小"
            "PlagiarismCheck" -> "查重检测失败，请重试或联系管理员"
            "Database" -> "数据操作失败，请重试"
            "Network" -> "网络连接失败，请检查网络设置"
            else -> "操作失败，请重试"
        }
    }

    /**
     * Show error to user (placeholder for snackbar/toast implementation)
     */
    private fun showErrorToUser(message: String, errorId: String) {
        logger.info("Showing error to user: $message (ID: $errorId)")
    }

    /**
     * Log detailed error information
     */
    private fun logErrorDetails(error: Throwable, context: String, errorId: String) {
        logger.error("ERROR_DETAILS [ID: $errorId, Context: $context]", error)
    }

    /**
     * Generate unique error ID
     */
    private fun generateErrorId(): String {
        return "ERR-${System.currentTimeMillis()}-${(1000..9999).random()}"
    }

    /**
     * Handle async error in a separate scope
     */
    fun handleAsyncError(error: Throwable, context: String) {
        CoroutineScope(Dispatchers.IO).launch {
            handleError(error, context)
        }
    }
}

/**
 * Composable to handle errors in UI
 */
@Composable
fun ErrorHandler(
    error: Throwable?,
    context: String,
    onErrorHandled: () -> Unit = {},
    globalErrorHandler: GlobalErrorHandler? = null
) {
    val appContext = LocalContext.current

    LaunchedEffect(error) {
        error?.let {
            globalErrorHandler?.handleError(it, context)
            onErrorHandled()
        }
    }
}
