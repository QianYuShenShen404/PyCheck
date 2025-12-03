package com.example.codechecker.domain.usecase.admin

import com.example.codechecker.domain.model.AdminSetting
import com.example.codechecker.domain.model.LogLevel
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
 * Unit tests for GetAdminSettingsUseCase
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetAdminSettingsUseCaseTest {

    @Mock
    private lateinit var adminSettingsRepository: AdminSettingsRepository

    private lateinit var getAdminSettingsUseCase: GetAdminSettingsUseCase

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getAdminSettingsUseCase = GetAdminSettingsUseCase(adminSettingsRepository, testDispatcher)
    }

    @Test
    fun `execute should return admin settings successfully`() = runTest(testDispatcher) {
        // Given
        val mockSettings = AdminSetting(
            id = 1L,
            key = "similarity_threshold",
            value = "0.8",
            description = "Similarity threshold for plagiarism detection",
            category = "Algorithm",
            isEditable = true
        )

        whenever(adminSettingsRepository.getAllSettings()).thenReturn(listOf(mockSettings))

        // When
        val result = getAdminSettingsUseCase.execute()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(listOf(mockSettings), result.getOrNull())
        verify(adminSettingsRepository).getAllSettings()
    }

    @Test
    fun `execute should return multiple settings`() = runTest(testDispatcher) {
        // Given
        val mockSettings = listOf(
            AdminSetting(
                id = 1L,
                key = "similarity_threshold",
                value = "0.8",
                description = "Similarity threshold",
                category = "Algorithm",
                isEditable = true
            ),
            AdminSetting(
                id = 2L,
                key = "log_level",
                value = "INFO",
                description = "Logging level",
                category = "System",
                isEditable = true
            ),
            AdminSetting(
                id = 3L,
                key = "retention_days",
                value = "90",
                description = "Data retention days",
                category = "Data",
                isEditable = true
            )
        )

        whenever(adminSettingsRepository.getAllSettings()).thenReturn(mockSettings)

        // When
        val result = getAdminSettingsUseCase.execute()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(3, result.getOrNull()?.size)
        verify(adminSettingsRepository).getAllSettings()
    }

    @Test
    fun `execute should return empty list when no settings exist`() = runTest(testDispatcher) {
        // Given
        val emptyList = emptyList<AdminSetting>()
        whenever(adminSettingsRepository.getAllSettings()).thenReturn(emptyList)

        // When
        val result = getAdminSettingsUseCase.execute()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(emptyList, result.getOrNull())
        assertEquals(0, result.getOrNull()?.size)
        verify(adminSettingsRepository).getAllSettings()
    }

    @Test
    fun `execute should return failure when repository throws exception`() = runTest(testDispatcher) {
        // Given
        val exception = RuntimeException("Database error")
        whenever(adminSettingsRepository.getAllSettings()).thenThrow(exception)

        // When
        val result = getAdminSettingsUseCase.execute()

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(adminSettingsRepository).getAllSettings()
    }

    @Test
    fun `execute should return settings with different categories`() = runTest(testDispatcher) {
        // Given
        val mockSettings = listOf(
            AdminSetting(1L, "similarity_threshold", "0.8", "Threshold", "Algorithm", true),
            AdminSetting(2L, "log_level", "INFO", "Level", "System", true),
            AdminSetting(3L, "retention_days", "90", "Retention", "Data", true),
            AdminSetting(4L, "fast_compare", "true", "Fast mode", "Algorithm", true)
        )

        whenever(adminSettingsRepository.getAllSettings()).thenReturn(mockSettings)

        // When
        val result = getAdminSettingsUseCase.execute()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(4, result.getOrNull()?.size)
        verify(adminSettingsRepository).getAllSettings()
    }

    @Test
    fun `execute should handle settings with null values`() = runTest(testDispatcher) {
        // Given
        val mockSettings = listOf(
            AdminSetting(
                id = 1L,
                key = "nullable_setting",
                value = null,
                description = "Nullable setting",
                category = "Test",
                isEditable = true
            )
        )

        whenever(adminSettingsRepository.getAllSettings()).thenReturn(mockSettings)

        // When
        val result = getAdminSettingsUseCase.execute()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertNull(result.getOrNull()?.get(0)?.value)
        verify(adminSettingsRepository).getAllSettings()
    }
}
