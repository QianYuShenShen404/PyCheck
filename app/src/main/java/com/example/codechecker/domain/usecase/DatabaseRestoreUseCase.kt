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
 * Use case for restoring database from backup
 */
@Singleton
class DatabaseRestoreUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val auditLogger: AuditLogger
) {
    suspend fun execute(backupPath: String): Result<Unit> {
        return invoke(backupPath = backupPath, adminUserId = 0)
    }
    suspend operator fun invoke(
        backupPath: String,
        adminUserId: Long,
        onProgress: ((Int) -> Unit)? = null
    ): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                onProgress?.invoke(10)

                val backupFile = File(backupPath)
                if (!backupFile.exists()) {
                    throw Exception("备份文件不存在: $backupPath")
                }

                onProgress?.invoke(30)

                // Close database connection
                database.close()

                onProgress?.invoke(50)

                val dbFile = context.getDatabasePath("codechecker_database")

                // Create backup of current database
                if (dbFile.exists()) {
                    val currentBackup = File(backupFile.parent, "current_backup_${System.currentTimeMillis()}.db")
                    FileInputStream(dbFile).use { input ->
                        FileOutputStream(currentBackup).use { output ->
                            val buffer = ByteArray(8192)
                            while (true) {
                                val bytesRead = input.read(buffer)
                                if (bytesRead == -1) break
                                output.write(buffer, 0, bytesRead)
                            }
                        }
                    }
                }

                onProgress?.invoke(70)

                // Restore from backup
                FileInputStream(backupFile).use { input ->
                    FileOutputStream(dbFile).use { output ->
                        val buffer = ByteArray(8192)
                        var totalBytes = 0
                        val fileSize = backupFile.length()

                        while (true) {
                            val bytesRead = input.read(buffer)
                            if (bytesRead == -1) break

                            output.write(buffer, 0, bytesRead)
                            totalBytes += bytesRead

                            val progress = 70 + (totalBytes * 20 / fileSize).toInt()
                            onProgress?.invoke(progress)
                        }
                    }
                }

                onProgress?.invoke(95)

                auditLogger.log(
                    adminUserId = adminUserId,
                    action = com.example.codechecker.domain.model.AuditAction.DATABASE_RESTORE,
                    targetType = com.example.codechecker.domain.model.AuditTargetType.SYSTEM,
                    result = com.example.codechecker.domain.model.AuditResult.SUCCESS,
                    details = "Restored database from: $backupPath"
                )

                onProgress?.invoke(100)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            auditLogger.logError(
                adminUserId = adminUserId,
                action = com.example.codechecker.domain.model.AuditAction.DATABASE_RESTORE,
                targetType = com.example.codechecker.domain.model.AuditTargetType.SYSTEM,
                error = e,
                details = "Failed to restore database from: $backupPath"
            )
            Result.failure(e)
        }
    }
}
