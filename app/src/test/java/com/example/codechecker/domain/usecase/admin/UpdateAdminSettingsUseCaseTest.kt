package com.example.codechecker.domain.usecase.admin

import com.example.codechecker.domain.model.AdminSetting
import com.example.codechecker.domain.repository.AdminSettingsRepository
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
 * Unit tests for UpdateAdminSettingsUseCase
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UpdateAdminSettingsUseCaseTest {

    @Mock
    private lateinit var adminSettingsRepository: AdminSettingsRepository

    private lateinit var updateAdminSettingsUseCase: UpdateAdminSettingsUseCase

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        updateAdminSettingsUseCase = UpdateAdminSettingsUseCase(adminSettingsRepository, testDispatcher)
    }

    @Test
    fun `execute should update setting successfully`() = runTest(testDispatcher) {
        // Given
        val key = "similarity_threshold"
        val value = "0.9"

        val updatedSetting = AdminSetting(
            id = 1L,
            key = key,
            value = value,
            description = "Similarity threshold",
            category = "Algorithm",
            isEditable = true
        )

        whenever(adminSettingsRepository.updateSetting(key, value)).thenReturn(updatedSetting)

        // When
        val result = updateAdminSettingsUseCase.execute(key, value)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(updatedSetting, result.getOrNull())
        verify(adminSettingsRepository).updateSetting(key, value)
    }

    @Test
    fun `execute should update log level setting`() = runTest(testDispatcher) {
        // Given
        val key = "log_level"
        val value = "DEBUG"

        val updatedSetting = AdminSetting(
            id = 2L,
            key = key,
            value = value,
            description = "Logging level",
            category = "System",
            isEditable = true
        )

        whenever(adminSettingsRepository.updateSetting(key, value)).thenReturn(updatedSetting)

        // When
        val result = updateAdminSettingsUseCase.execute(key, value)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("DEBUG", result.getOrNull()?.value)
        verify(adminSettingsRepository).updateSetting(key, value)
    }

    @Test
    fun `execute should update retention days setting`() = runTest(testDispatcher) {
        // Given
        val key = "retention_days"
        val value = "180"

        val updatedSetting = AdminSetting(
            id = 3L,
            key = key,
            value = value,
            description = "Data retention days",
            category = "Data",
            isEditable = true
        )

        whenever(adminSettingsRepository.updateSetting(key, value)).thenReturn(updatedSetting)

        // When
        val result = updateAdminSettingsUseCase.execute(key, value)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("180", result.getOrNull()?.value)
        verify(adminSettingsRepository).updateSetting(key, value)
    }

    @Test
    fun `execute should update fast compare setting`() = runTest(testDispatcher) {
        // Given
        val key = "fast_compare"
        val value = "true"

        val updatedSetting = AdminSetting(
            id = 4L,
            key = key,
            value = value,
            description = "Fast compare mode",
            category = "Algorithm",
            isEditable = true
        )

        whenever(adminSettingsRepository.updateSetting(key, value)).thenReturn(updatedSetting)

        // When
        val result = updateAdminSettingsUseCase.execute(key, value)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("true", result.getOrNull()?.value)
        verify(adminSettingsRepository).updateSetting(key, value)
    }

    @Test
    fun `execute should return failure when setting not found`() = runTest(testDispatcher) {
        // Given
        val key = "nonexistent_setting"
        val value = "some_value"
        val exception = IllegalStateException("Setting not found")

        whenever(adminSettingsRepository.updateSetting(key, value)).thenThrow(exception)

        // When
        val result = updateAdminSettingsUseCase.execute(key, value)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(adminSettingsRepository).updateSetting(key, value)
    }

    @Test
    fun `execute should return failure when setting is not editable`() = runTest(testDispatcher) {
        // Given
        val key = "system_locked"
        val value = "new_value"
        val exception = IllegalStateException("Setting is not editable")

        whenever(adminSettingsRepository.updateSetting(key, value)).thenThrow(exception)

        // When
        val result = updateAdminSettingsUseCase.execute(key, value)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(adminSettingsRepository).updateSetting(key, value)
    }

    @Test
    fun `execute should return failure when repository throws exception`() = runTest(testDispatcher) {
        // Given
        val key = "test_setting"
        val value = "test_value"
        val exception = RuntimeException("Database error")

        whenever(adminSettingsRepository.updateSetting(key, value)).thenThrow(exception)

        // When
        val result = updateAdminSettingsUseCase.execute(key, value)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(adminSettingsRepository).updateSetting(key, value)
    }

    @Test
    fun `execute should handle empty value`() = runTest(testDispatcher) {
        // Given
        val key = "test_setting"
        val value = ""

        // When
        val result = updateAdminSettingsUseCase.execute(key, value)

        // Then
        verify(adminSettingsRepository, never()).updateSetting(any(), any())
        assertTrue(result.isFailure)
    }

    @Test
    fun `execute should handle whitespace value`() = runTest(testDispatcher) {
        // Given
        val key = "test_setting"
        val value = "   "

        // When
        val result = updateAdminSettingsUseCase.execute(key, value)

        // Then
        verify(adminSettingsRepository, never()).updateSetting(any(), any())
        assertTrue(result.isFailure)
    }

    @Test
    fun `execute should accept valid numeric values`() = runTest(testDispatcher) {
        // Given
        val key = "numeric_setting"
        val validValues = listOf("0", "1", "10", "100", "999")

        val updatedSetting = AdminSetting(
            id = 1L,
            key = key,
            value = "100",
            description = "Numeric setting",
            category = "Test",
            isEditable = true
        )

        whenever(adminSettingsRepository.updateSetting(any(), any())).thenReturn(updatedSetting)

        // Test all valid values
        validValues.forEach { value ->
            val result = updateAdminSettingsUseCase.execute(key, value)
            assertTrue(result.isSuccess, "Should accept numeric value: $value")
        }
    }

    @Test
    fun `execute should accept boolean values`() = runTest(testDispatcher) {
        // Given
        val key = "boolean_setting"
        val booleanValues = listOf("true", "false")

        val updatedSetting = AdminSetting(
            id = 1L,
            key = key,
            value = "true",
            description = "Boolean setting",
            category = "Test",
            isEditable = true
        )

        whenever(adminSettingsRepository.updateSetting(any(), any())).thenReturn(updatedSetting)

        // Test boolean values
        booleanValues.forEach { value ->
            val result = updateAdminSettingsUseCase.execute(key, value)
            assertTrue(result.isSuccess, "Should accept boolean value: $value")
        }
    }

    @Test
    fun `execute should accept decimal values`() = runTest(testDispatcher) {
        // Given
        val key = "threshold"
        val decimalValues = listOf("0.0", "0.5", "0.75", "1.0", "0.99")

        val updatedSetting = AdminSetting(
            id = 1L,
            key = key,
            value = "0.5",
            description = "Decimal setting",
            category = "Test",
            isEditable = true
        )

        whenever(adminSettingsRepository.updateSetting(any(), any())).thenReturn(updatedSetting)

        // Test decimal values
        decimalValues.forEach { value ->
            val result = updateAdminSettingsUseCase.execute(key, value)
            assertTrue(result.isSuccess, "Should accept decimal value: $value")
        }
    }
}
