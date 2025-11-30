package com.example.codechecker.util

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Performance monitoring utility
 * Tracks app performance metrics and detects issues
 */
@Singleton
class PerformanceMonitor @Inject constructor(
    private val logger: Logger
) {

    private val metrics = mutableMapOf<String, Long>()
    private val frameTimes = mutableListOf<Long>()
    private var startTime: Long = 0

    companion object {
        private const val MAX_FRAME_TIME_SAMPLES = 60
        private const val TAG = "PerformanceMonitor"
    }

    /**
     * Start performance tracking for a specific operation
     */
    fun startTracking(operation: String) {
        metrics[operation] = System.currentTimeMillis()
        logger.debug("Started tracking: $operation")
    }

    /**
     * End performance tracking and log the result
     */
    fun endTracking(operation: String): Long {
        val startTime = metrics[operation]
        if (startTime != null) {
            val duration = System.currentTimeMillis() - startTime
            metrics.remove(operation)

            logger.debug("Performance [$operation]: ${duration}ms")

            checkPerformance(operation, duration)
            return duration
        }
        logger.warning("No start time found for operation: $operation")
        return 0
    }

    /**
     * Track frame render time
     */
    fun trackFrameTime(frameTime: Long) {
        frameTimes.add(frameTime)
        if (frameTimes.size > MAX_FRAME_TIME_SAMPLES) {
            frameTimes.removeAt(0)
        }
    }

    /**
     * Get average frame time
     */
    fun getAverageFrameTime(): Double {
        return if (frameTimes.isNotEmpty()) {
            frameTimes.average()
        } else {
            0.0
        }
    }

    /**
     * Get frame time variance
     */
    fun getFrameTimeVariance(): Double {
        if (frameTimes.size < 2) return 0.0
        val average = frameTimes.average()
        return frameTimes.map { (it - average) * (it - average) }.average()
    }

    /**
     * Check if app startup is complete and track it
     */
    fun trackAppStartup(startTime: Long) {
        this.startTime = startTime
        CoroutineScope(Dispatchers.IO).launch {
            val startupTime = System.currentTimeMillis() - startTime
            logger.info("APP_STARTUP: ${startupTime}ms")

            checkPerformance("AppStartup", startupTime)
        }
    }

    /**
     * Track screen transition
     */
    fun trackScreenTransition(screenName: String, duration: Long) {
        logger.debug("SCREEN_TRANSITION [$screenName]: ${duration}ms")
        checkPerformance("ScreenTransition:$screenName", duration)
    }

    /**
     * Track database operation
     */
    fun trackDatabaseOperation(operation: String, duration: Long) {
        logger.debug("DB_OPERATION [$operation]: ${duration}ms")
        if (duration > 100) {
            logger.warning("Slow database operation detected: $operation took ${duration}ms")
        }
    }

    /**
     * Track plagiarism detection performance
     */
    fun trackPlagiarismDetection(submissionCount: Long, duration: Long) {
        val rate = if (duration > 0) (submissionCount * 1000) / duration else 0
        logger.info("PLAGIARISM_CHECK: $submissionCount submissions in ${duration}ms (${rate} submissions/sec)")

        checkPerformance("PlagiarismDetection:$submissionCount", duration)
    }

    /**
     * Check performance against thresholds
     */
    private fun checkPerformance(operation: String, duration: Long) {
        when {
            operation.startsWith("AppStartup") && duration > 3000 -> {
                logger.warning("SLOW_STARTUP: App startup took ${duration}ms (target: <3000ms)")
            }
            operation.startsWith("ScreenTransition") && duration > 300 -> {
                logger.warning("SLOW_TRANSITION: Screen transition took ${duration}ms (target: <300ms)")
            }
            operation.startsWith("PlagiarismDetection") && duration > 30000 -> {
                logger.warning("SLOW_PLAGIARISM: Detection took ${duration}ms (target: <30000ms)")
            }
            operation.startsWith("DB_OPERATION") && duration > 200 -> {
                logger.warning("SLOW_DB: Operation took ${duration}ms (target: <200ms)")
            }
        }
    }

    /**
     * Get performance report
     */
    fun getPerformanceReport(): String {
        val avgFrameTime = getAverageFrameTime()
        val frameTimeVariance = getFrameTimeVariance()
        val isRunningSmoothly = avgFrameTime < 16.67 // 60 FPS target

        return buildString {
            appendLine("=== Performance Report ===")
            appendLine("Average Frame Time: ${String.format("%.2f", avgFrameTime)}ms")
            appendLine("Frame Time Variance: ${String.format("%.2f", frameTimeVariance)}")
            appendLine("UI Status: ${if (isRunningSmoothly) "Smooth (60 FPS)" else "Janky (<60 FPS)"}")
            appendLine("Active Metrics: ${metrics.size}")
            metrics.forEach { (operation, start) ->
                val duration = System.currentTimeMillis() - start
                appendLine("  - $operation: ${duration}ms (ongoing)")
            }
            appendLine("========================")
        }
    }

    /**
     * Clear all metrics
     */
    fun clearMetrics() {
        metrics.clear()
        frameTimes.clear()
        logger.debug("Performance metrics cleared")
    }
}
