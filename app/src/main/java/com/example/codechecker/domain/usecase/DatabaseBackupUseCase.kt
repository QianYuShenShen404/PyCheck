package com.example.codechecker.domain.usecase

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.example.codechecker.data.local.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for creating database backups
 */
@Singleton
class DatabaseBackupUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auditLogger: AuditLogger
) {
    suspend fun execute(): Result<String> {
        return invoke(adminUserId = 0)
    }
    suspend operator fun invoke(
        adminUserId: Long,
        onProgress: ((Int) -> Unit)? = null
    ): Result<String> {
        return try {
            withContext(Dispatchers.IO) {
                onProgress?.invoke(10)

                val dbFile = context.getDatabasePath("codechecker_database")
                if (!dbFile.exists()) {
                    throw Exception("数据库文件不存在")
                }

                onProgress?.invoke(30)

                val timestamp = System.currentTimeMillis()
                val backupDir = File(context.getExternalFilesDir(null), "CodeChecker/Backups")
                if (!backupDir.exists()) {
                    backupDir.mkdirs()
                }

                val backupFile = File(backupDir, "codechecker_backup_$timestamp.db")

                onProgress?.invoke(50)

                // Copy database file
                FileInputStream(dbFile).use { input ->
                    FileOutputStream(backupFile).use { output ->
                        val buffer = ByteArray(8192)
                        var totalBytes = 0
                        val fileSize = dbFile.length()

                        while (true) {
                            val bytesRead = input.read(buffer)
                            if (bytesRead == -1) break

                            output.write(buffer, 0, bytesRead)
                            totalBytes += bytesRead

                            // Update progress
                            val progress = 50 + (totalBytes * 40 / fileSize).toInt()
                            onProgress?.invoke(progress)
                        }
                    }
                }

                onProgress?.invoke(95)

                auditLogger.log(
                    adminUserId = adminUserId,
                    action = com.example.codechecker.domain.model.AuditAction.DATABASE_BACKUP,
                    targetType = com.example.codechecker.domain.model.AuditTargetType.SYSTEM,
                    result = com.example.codechecker.domain.model.AuditResult.SUCCESS,
                    details = "Created database backup: ${backupFile.absolutePath}"
                )

                onProgress?.invoke(100)
                Result.success(backupFile.absolutePath)
            }
        } catch (e: Exception) {
            auditLogger.logError(
                adminUserId = adminUserId,
                action = com.example.codechecker.domain.model.AuditAction.DATABASE_BACKUP,
                targetType = com.example.codechecker.domain.model.AuditTargetType.SYSTEM,
                error = e,
                details = "Failed to create database backup"
            )
            Result.failure(e)
        }
    }
}
