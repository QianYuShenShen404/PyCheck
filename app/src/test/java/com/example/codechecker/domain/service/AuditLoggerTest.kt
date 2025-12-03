package com.example.codechecker.domain.service

import com.example.codechecker.data.repository.AdminAuditLogRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import kotlin.test.*

/**
 * Unit tests for AuditLogger
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AuditLoggerTest {

    @Mock
    private lateinit var auditLogRepository: AdminAuditLogRepository

    private lateinit var auditLogger: AuditLogger

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        auditLogger = AuditLogger(auditLogRepository, testDispatcher)
    }

    @Test
    fun `log should create audit entry successfully`() = runTest(testDispatcher) {
        // Given
        val action = "USER_CREATE"
        val result = "SUCCESS"
        val category = "User"
        val userId = 1L
        val details = "User created successfully"

        // When
        auditLogger.log(action, result, category, userId, details)

        // Then
        verify(auditLogRepository).insertLog(
            action = eq(action),
            result = eq(result),
            category = eq(category),
            userId = eq(userId),
            details = eq(details)
        )
    }

    @Test
    fun `log should handle null userId`() = runTest(testDispatcher) {
        // Given
        val action = "SYSTEM_BACKUP"
        val result = "SUCCESS"
        val category = "System"
        val userId = null
        val details = "Database backup completed"

        // When
        auditLogger.log(action, result, category, userId, details)

        // Then
        verify(auditLogRepository).insertLog(
            action = eq(action),
            result = eq(result),
            category = eq(category),
            userId = isNull(),
            details = eq(details)
        )
    }

    @Test
    fun `log should log user login action`() = runTest(testDispatcher) {
        // Given
        val action = "USER_LOGIN"
        val result = "SUCCESS"
        val category = "Authentication"
        val userId = 2L
        val details = "User logged in successfully"

        // When
        auditLogger.log(action, result, category, userId, details)

        // Then
        verify(auditLogRepository).insertLog(
            action = eq(action),
            result = eq(result),
            category = eq(category),
            userId = eq(userId),
            details = eq(details)
        )
    }

    @Test
    fun `log should log failed login attempt`() = runTest(testDispatcher) {
        // Given
        val action = "USER_LOGIN"
        val result = "FAILED"
        val category = "Authentication"
        val userId = null
        val details = "Invalid credentials"

        // When
        auditLogger.log(action, result, category, userId, details)

        // Then
        verify(auditLogRepository).insertLog(
            action = eq(action),
            result = eq(result),
            category = eq(category),
            userId = isNull(),
            details = eq(details)
        )
    }

    @Test
    fun `log should log role change`() = runTest(testDispatcher) {
        // Given
        val action = "ROLE_CHANGE"
        val result = "SUCCESS"
        val category = "User"
        val userId = 3L
        val details = "Role changed from STUDENT to TEACHER"

        // When
        auditLogger.log(action, result, category, userId, details)

        // Then
        verify(auditLogRepository).insertLog(
            action = eq(action),
            result = eq(result),
            category = eq(category),
            userId = eq(userId),
            details = eq(details)
        )
    }

    @Test
    fun `log should log password reset`() = runTest(testDispatcher) {
        // Given
        val action = "PASSWORD_RESET"
        val result = "SUCCESS"
        val category = "Security"
        val userId = 1L
        val details = "Password reset by admin"

        // When
        auditLogger.log(action, result, category, userId, details)

        // Then
        verify(auditLogRepository).insertLog(
            action = eq(action),
            result = eq(result),
            category = eq(category),
            userId = eq(userId),
            details = eq(details)
        )
    }

    @Test
    fun `log should log data export`() = runTest(testDispatcher) {
        // Given
        val action = "DATA_EXPORT"
        val result = "SUCCESS"
        val category = "Data"
        val userId = 1L
        val details = "Exported data in JSON format"

        // When
        auditLogger.log(action, result, category, userId, details)

        // Then
        verify(auditLogRepository).insertLog(
            action = eq(action),
            result = eq(result),
            category = eq(category),
            userId = eq(userId),
            details = eq(details)
        )
    }

    @Test
    fun `log should log data import`() = runTest(testDispatcher) {
        // Given
        val action = "DATA_IMPORT"
        val result = "SUCCESS"
        val category = "Data"
        val userId = 1L
        val details = "Imported 50 users from CSV"

        // When
        auditLogger.log(action, result, category, userId, details)

        // Then
        verify(auditLogRepository).insertLog(
            action = eq(action),
            result = eq(result),
            category = eq(category),
            userId = eq(userId),
            details = eq(details)
        )
    }

    @Test
    fun `log should log database backup`() = runTest(testDispatcher) {
        // Given
        val action = "DATABASE_BACKUP"
        val result = "SUCCESS"
        val category = "Database"
        val userId = 1L
        val details = "Database backup completed successfully"

        // When
        auditLogger.log(action, result, category, userId, details)

        // Then
        verify(auditLogRepository).insertLog(
            action = eq(action),
            result = eq(result),
            category = eq(category),
            userId = eq(userId),
            details = eq(details)
        )
    }

    @Test
    fun `log should handle repository exception`() = runTest(testDispatcher) {
        // Given
        val action = "TEST_ACTION"
        val result = "SUCCESS"
        val category = "Test"
        val userId = 1L
        val details = "Test details"
        val exception = RuntimeException("Database error")

        doThrow(exception).whenever(auditLogRepository).insertLog(
            any(), any(), any(), any(), any()
        )

        // When - should not throw exception
        auditLogger.log(action, result, category, userId, details)

        // Then - should still call repository even if it fails
        verify(auditLogRepository).insertLog(
            action = eq(action),
            result = eq(result),
            category = eq(category),
            userId = eq(userId),
            details = eq(details)
        )
    }

    @Test
    fun `log should handle empty details`() = runTest(testDispatcher) {
        // Given
        val action = "USER_DELETE"
        val result = "SUCCESS"
        val category = "User"
        val userId = 2L
        val details = ""

        // When
        auditLogger.log(action, result, category, userId, details)

        // Then
        verify(auditLogRepository).insertLog(
            action = eq(action),
            result = eq(result),
            category = eq(category),
            userId = eq(userId),
            details = eq(details)
        )
    }

    @Test
    fun `log should handle long details string`() = runTest(testDispatcher) {
        // Given
        val action = "BULK_OPERATION"
        val result = "SUCCESS"
        val category = "Data"
        val userId = 1L
        val details = "a".repeat(1000) // Long string

        // When
        auditLogger.log(action, result, category, userId, details)

        // Then
        verify(auditLogRepository).insertLog(
            action = eq(action),
            result = eq(result),
            category = eq(category),
            userId = eq(userId),
            details = eq(details)
        )
    }

    @Test
    fun `log should handle various action types`() = runTest(testDispatcher) {
        // Given
        val actions = listOf(
            "USER_CREATE",
            "USER_UPDATE",
            "USER_DELETE",
            "USER_LOGIN",
            "USER_LOGOUT",
            "ROLE_CHANGE",
            "PASSWORD_RESET",
            "SETTINGS_UPDATE",
            "DATA_EXPORT",
            "DATA_IMPORT",
            "DATABASE_BACKUP",
            "DATABASE_RESTORE"
        )

        // When
        actions.forEach { action ->
            auditLogger.log(action, "SUCCESS", "Test", 1L, "Test details")
        }

        // Then
        actions.forEach { action ->
            verify(auditLogRepository).insertLog(
                action = eq(action),
                result = eq("SUCCESS"),
                category = eq("Test"),
                userId = eq(1L),
                details = eq("Test details")
            )
        }
    }
}
