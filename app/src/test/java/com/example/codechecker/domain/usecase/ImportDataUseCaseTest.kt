package com.example.codechecker.domain.usecase

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
 * Unit tests for ImportDataUseCase
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ImportDataUseCaseTest {

    @Mock
    private lateinit var dataImporter: DataImporter

    private lateinit var importDataUseCase: ImportDataUseCase

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        importDataUseCase = ImportDataUseCase(dataImporter, testDispatcher)
    }

    @Test
    fun `execute should import data from JSON successfully`() = runTest(testDispatcher) {
        // Given
        val format = "JSON"
        val importedCount = 50

        whenever(dataImporter.importFromJson()).thenReturn(importedCount)

        // When
        val result = importDataUseCase.execute(format)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(importedCount, result.getOrNull())
        verify(dataImporter).importFromJson()
        verifyNoMoreInteractions(dataImporter)
    }

    @Test
    fun `execute should import data from CSV successfully`() = runTest(testDispatcher) {
        // Given
        val format = "CSV"
        val importedCount = 100

        whenever(dataImporter.importFromCsv()).thenReturn(importedCount)

        // When
        val result = importDataUseCase.execute(format)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(importedCount, result.getOrNull())
        verify(dataImporter).importFromCsv()
        verifyNoMoreInteractions(dataImporter)
    }

    @Test
    fun `execute should import data from XML successfully`() = runTest(testDispatcher) {
        // Given
        val format = "XML"
        val importedCount = 25

        whenever(dataImporter.importFromXml()).thenReturn(importedCount)

        // When
        val result = importDataUseCase.execute(format)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(importedCount, result.getOrNull())
        verify(dataImporter).importFromXml()
        verifyNoMoreInteractions(dataImporter)
    }

    @Test
    fun `execute should return failure when format is not supported`() = runTest(testDispatcher) {
        // Given
        val format = "PDF"

        // When
        val result = importDataUseCase.execute(format)

        // Then
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
        verifyNoInteractions(dataImporter)
    }

    @Test
    fun `execute should return zero when importing empty file`() = runTest(testDispatcher) {
        // Given
        val format = "JSON"
        val importedCount = 0

        whenever(dataImporter.importFromJson()).thenReturn(importedCount)

        // When
        val result = importDataUseCase.execute(format)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull())
        verify(dataImporter).importFromJson()
    }

    @Test
    fun `execute should return failure when JSON import fails`() = runTest(testDispatcher) {
        // Given
        val format = "JSON"
        val exception = RuntimeException("Invalid JSON format")
        whenever(dataImporter.importFromJson()).thenThrow(exception)

        // When
        val result = importDataUseCase.execute(format)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(dataImporter).importFromJson()
    }

    @Test
    fun `execute should return failure when CSV import fails`() = runTest(testDispatcher) {
        // Given
        val format = "CSV"
        val exception = RuntimeException("Invalid CSV format")
        whenever(dataImporter.importFromCsv()).thenThrow(exception)

        // When
        val result = importDataUseCase.execute(format)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(dataImporter).importFromCsv()
    }

    @Test
    fun `execute should return failure when XML import fails`() = runTest(testDispatcher) {
        // Given
        val format = "XML"
        val exception = RuntimeException("Invalid XML format")
        whenever(dataImporter.importFromXml()).thenThrow(exception)

        // When
        val result = importDataUseCase.execute(format)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(dataImporter).importFromXml()
    }

    @Test
    fun `execute should handle case-insensitive format`() = runTest(testDispatcher) {
        // Given
        val formats = listOf("json", "JSON", "Json")
        val importedCount = 50

        whenever(dataImporter.importFromJson()).thenReturn(importedCount)

        // Test all variations
        formats.forEach { format ->
            val result = importDataUseCase.execute(format)
            assertTrue(result.isSuccess, "Should handle format: $format")
        }

        verify(dataImporter, times(3)).importFromJson()
    }

    @Test
    fun `execute should handle null format`() = runTest(testDispatcher) {
        // Given
        val format = null

        // When
        val result = importDataUseCase.execute(format)

        // Then
        assertTrue(result.isFailure)
        verifyNoInteractions(dataImporter)
    }

    @Test
    fun `execute should handle empty format`() = runTest(testDispatcher) {
        // Given
        val format = ""

        // When
        val result = importDataUseCase.execute(format)

        // Then
        assertTrue(result.isFailure)
        verifyNoInteractions(dataImporter)
    }

    @Test
    fun `execute should return positive count for successful import`() = runTest(testDispatcher) {
        // Given
        val format = "JSON"
        val importedCount = 123

        whenever(dataImporter.importFromJson()).thenReturn(importedCount)

        // When
        val result = importDataUseCase.execute(format)

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!! > 0)
    }

    @Test
    fun `execute should handle large import count`() = runTest(testDispatcher) {
        // Given
        val format = "CSV"
        val importedCount = 10000

        whenever(dataImporter.importFromCsv()).thenReturn(importedCount)

        // When
        val result = importDataUseCase.execute(format)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(10000, result.getOrNull())
        verify(dataImporter).importFromCsv()
    }

    @Test
    fun `execute should handle whitespace in format`() = runTest(testDispatcher) {
        // Given
        val format = "  JSON  "

        val importedCount = 50
        whenever(dataImporter.importFromJson()).thenReturn(importedCount)

        // When
        val result = importDataUseCase.execute(format)

        // Then
        assertTrue(result.isSuccess)
        verify(dataImporter).importFromJson()
    }

    @Test
    fun `execute should import all supported formats`() = runTest(testDispatcher) {
        // Given
        val supportedFormats = listOf("JSON", "CSV", "XML")
        val importedCount = 50

        whenever(dataImporter.importFromJson()).thenReturn(importedCount)
        whenever(dataImporter.importFromCsv()).thenReturn(importedCount)
        whenever(dataImporter.importFromXml()).thenReturn(importedCount)

        // Test all supported formats
        supportedFormats.forEach { format ->
            val result = importDataUseCase.execute(format)
            assertTrue(result.isSuccess, "Should support format: $format")
        }
    }

    @Test
    fun `execute should handle import errors gracefully`() = runTest(testDispatcher) {
        // Given
        val format = "JSON"
        val exception = RuntimeException("File not found")

        whenever(dataImporter.importFromJson()).thenThrow(exception)

        // When
        val result = importDataUseCase.execute(format)

        // Then
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }
}
