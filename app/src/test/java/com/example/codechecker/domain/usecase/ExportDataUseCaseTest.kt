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
 * Unit tests for ExportDataUseCase
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ExportDataUseCaseTest {

    @Mock
    private lateinit var dataExporter: DataExporter

    private lateinit var exportDataUseCase: ExportDataUseCase

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        exportDataUseCase = ExportDataUseCase(dataExporter, testDispatcher)
    }

    @Test
    fun `execute should export data in JSON format successfully`() = runTest(testDispatcher) {
        // Given
        val format = "JSON"
        val filePath = "/storage/exports/data_export.json"

        whenever(dataExporter.exportToJson()).thenReturn(filePath)

        // When
        val result = exportDataUseCase.execute(format)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(filePath, result.getOrNull())
        verify(dataExporter).exportToJson()
        verifyNoMoreInteractions(dataExporter)
    }

    @Test
    fun `execute should export data in CSV format successfully`() = runTest(testDispatcher) {
        // Given
        val format = "CSV"
        val filePath = "/storage/exports/data_export.csv"

        whenever(dataExporter.exportToCsv()).thenReturn(filePath)

        // When
        val result = exportDataUseCase.execute(format)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(filePath, result.getOrNull())
        verify(dataExporter).exportToCsv()
        verifyNoMoreInteractions(dataExporter)
    }

    @Test
    fun `execute should export data in XML format successfully`() = runTest(testDispatcher) {
        // Given
        val format = "XML"
        val filePath = "/storage/exports/data_export.xml"

        whenever(dataExporter.exportToXml()).thenReturn(filePath)

        // When
        val result = exportDataUseCase.execute(format)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(filePath, result.getOrNull())
        verify(dataExporter).exportToXml()
        verifyNoMoreInteractions(dataExporter)
    }

    @Test
    fun `execute should return failure when format is not supported`() = runTest(testDispatcher) {
        // Given
        val format = "PDF"

        // When
        val result = exportDataUseCase.execute(format)

        // Then
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
        verifyNoInteractions(dataExporter)
    }

    @Test
    fun `execute should return failure when JSON export fails`() = runTest(testDispatcher) {
        // Given
        val format = "JSON"
        val exception = RuntimeException("Export failed")
        whenever(dataExporter.exportToJson()).thenThrow(exception)

        // When
        val result = exportDataUseCase.execute(format)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(dataExporter).exportToJson()
    }

    @Test
    fun `execute should return failure when CSV export fails`() = runTest(testDispatcher) {
        // Given
        val format = "CSV"
        val exception = RuntimeException("Export failed")
        whenever(dataExporter.exportToCsv()).thenThrow(exception)

        // When
        val result = exportDataUseCase.execute(format)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(dataExporter).exportToCsv()
    }

    @Test
    fun `execute should return failure when XML export fails`() = runTest(testDispatcher) {
        // Given
        val format = "XML"
        val exception = RuntimeException("Export failed")
        whenever(dataExporter.exportToXml()).thenThrow(exception)

        // When
        val result = exportDataUseCase.execute(format)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(dataExporter).exportToXml()
    }

    @Test
    fun `execute should handle case-insensitive format`() = runTest(testDispatcher) {
        // Given
        val formats = listOf("json", "JSON", "Json")
        val filePath = "/storage/exports/data.json"

        whenever(dataExporter.exportToJson()).thenReturn(filePath)

        // Test all variations
        formats.forEach { format ->
            val result = exportDataUseCase.execute(format)
            assertTrue(result.isSuccess, "Should handle format: $format")
        }

        verify(dataExporter, times(3)).exportToJson()
    }

    @Test
    fun `execute should handle null format`() = runTest(testDispatcher) {
        // Given
        val format = null

        // When
        val result = exportDataUseCase.execute(format)

        // Then
        assertTrue(result.isFailure)
        verifyNoInteractions(dataExporter)
    }

    @Test
    fun `execute should handle empty format`() = runTest(testDispatcher) {
        // Given
        val format = ""

        // When
        val result = exportDataUseCase.execute(format)

        // Then
        assertTrue(result.isFailure)
        verifyNoInteractions(dataExporter)
    }

    @Test
    fun `execute should return valid file path for successful export`() = runTest(testDispatcher) {
        // Given
        val format = "JSON"
        val filePath = "/storage/exports/users_2024_01_01.json"
        whenever(dataExporter.exportToJson()).thenReturn(filePath)

        // When
        val result = exportDataUseCase.execute(format)

        // Then
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        assertTrue(result.getOrNull()!!.endsWith(".json"))
    }

    @Test
    fun `execute should export all supported formats`() = runTest(testDispatcher) {
        // Given
        val supportedFormats = listOf("JSON", "CSV", "XML")
        val filePath = "/storage/exports/data"

        whenever(dataExporter.exportToJson()).thenReturn("$filePath.json")
        whenever(dataExporter.exportToCsv()).thenReturn("$filePath.csv")
        whenever(dataExporter.exportToXml()).thenReturn("$filePath.xml")

        // Test all supported formats
        supportedFormats.forEach { format ->
            val result = exportDataUseCase.execute(format)
            assertTrue(result.isSuccess, "Should support format: $format")
        }
    }

    @Test
    fun `execute should handle whitespace in format`() = runTest(testDispatcher) {
        // Given
        val format = "  JSON  "

        val filePath = "/storage/exports/data.json"
        whenever(dataExporter.exportToJson()).thenReturn(filePath)

        // When
        val result = exportDataUseCase.execute(format)

        // Then
        assertTrue(result.isSuccess)
        verify(dataExporter).exportToJson()
    }
}
