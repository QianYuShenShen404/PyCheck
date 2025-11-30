package com.example.codechecker.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.text.DecimalFormat
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for file operations
 */
@Singleton
class FileUtils @Inject constructor(
    private val context: Context
) {

    /**
     * Read file content as text
     */
    suspend fun readFile(file: File): String = withContext(Dispatchers.IO) {
        try {
            val content = StringBuilder()
            BufferedReader(FileReader(file)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    content.append(line).append("\n")
                }
            }
            content.toString()
        } catch (e: IOException) {
            throw IOException("Failed to read file: ${file.absolutePath}", e)
        }
    }

    /**
     * Read file content from URI
     */
    suspend fun readFileFromUri(uri: Uri): String = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.bufferedReader().use { reader ->
                    val content = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        content.append(line).append("\n")
                    }
                    content.toString()
                }
            } ?: throw IOException("Failed to open input stream")
        } catch (e: IOException) {
            throw IOException("Failed to read file from URI: $uri", e)
        }
    }

    /**
     * Get file extension
     */
    fun getFileExtension(fileName: String): String {
        val lastDotIndex = fileName.lastIndexOf('.')
        return if (lastDotIndex > 0 && lastDotIndex < fileName.length - 1) {
            fileName.substring(lastDotIndex + 1).lowercase()
        } else {
            ""
        }
    }

    /**
     * Check if file is Python file
     */
    fun isPythonFile(fileName: String): Boolean {
        val extension = getFileExtension(fileName)
        return extension == "py"
    }

    /**
     * Get file size in bytes
     */
    fun getFileSize(file: File): Long {
        return if (file.exists()) file.length() else 0
    }

    /**
     * Validate file size (max 1MB)
     */
    fun isFileSizeValid(file: File, maxSizeBytes: Long = 1024 * 1024): Boolean {
        return getFileSize(file) <= maxSizeBytes
    }

    /**
     * Get file name from URI
     */
    fun getFileNameFromUri(uri: Uri): String {
        var fileName = ""
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                fileName = cursor.getString(nameIndex)
            }
        }
        return fileName
    }

    /**
     * Get file size from URI
     */
    fun getFileSize(uri: Uri): Long? {
        return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (sizeIndex != -1 && cursor.moveToFirst()) {
                cursor.getLong(sizeIndex)
            } else {
                null
            }
        }
    }

    /**
     * Format file size for display
     */
    fun formatFileSize(bytes: Long): String {
        val df = DecimalFormat("#.##")
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${df.format(bytes / 1024.0)} KB"
            else -> "${df.format(bytes / (1024.0 * 1024.0))} MB"
        }
    }
}
