package com.example.codechecker.util

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for time operations
 */
@Singleton
class TimeUtils @Inject constructor() {

    /**
     * Get current timestamp in milliseconds
     */
    fun getCurrentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    /**
     * Format timestamp to readable date string
     */
    fun formatTimestamp(timestamp: Long, pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
        }
        return formatter.format(calendar.time)
    }

    /**
     * Format date and time (alias for formatTimestamp)
     */
    fun formatDateTime(timestamp: Long, pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
        return formatTimestamp(timestamp, pattern)
    }

    /**
     * Get relative time string (e.g., "2 hours ago")
     */
    fun getRelativeTime(timestamp: Long): String {
        val now = getCurrentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "刚刚"
            diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}分钟前"
            diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)}小时前"
            diff < TimeUnit.DAYS.toMillis(7) -> "${TimeUnit.MILLISECONDS.toDays(diff)}天前"
            else -> formatTimestamp(timestamp, "yyyy-MM-dd")
        }
    }

    /**
     * Check if timestamp is overdue
     */
    fun isOverdue(dueDate: Long, currentTime: Long = getCurrentTimeMillis()): Boolean {
        return dueDate > 0 && currentTime > dueDate
    }

    /**
     * Get remaining time until due date
     */
    fun getRemainingTime(dueDate: Long, currentTime: Long = getCurrentTimeMillis()): String {
        if (dueDate <= 0) return "无截止日期"
        
        val remaining = dueDate - currentTime
        return when {
            remaining <= 0 -> "已逾期"
            remaining < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(remaining)}分钟"
            remaining < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(remaining)}小时"
            remaining < TimeUnit.DAYS.toMillis(7) -> "${TimeUnit.MILLISECONDS.toDays(remaining)}天"
            else -> "${TimeUnit.MILLISECONDS.toDays(remaining) / 7}周"
        }
    }
}
