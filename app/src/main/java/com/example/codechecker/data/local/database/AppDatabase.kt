package com.example.codechecker.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.codechecker.data.local.dao.AdminAuditLogDao
import com.example.codechecker.data.local.dao.AdminSettingDao
import com.example.codechecker.data.local.dao.AssignmentDao
import com.example.codechecker.data.local.dao.ReportDao
import com.example.codechecker.data.local.dao.SubmissionDao
import com.example.codechecker.data.local.dao.UserDao
import com.example.codechecker.data.local.dao.SimilarityDao
import com.example.codechecker.data.local.entity.AdminAuditLogEntity
import com.example.codechecker.data.local.entity.AdminSettingEntity
import com.example.codechecker.data.local.entity.AssignmentEntity
import com.example.codechecker.data.local.entity.ReportEntity
import com.example.codechecker.data.local.entity.SubmissionEntity
import com.example.codechecker.data.local.entity.SimilarityEntity
import com.example.codechecker.data.local.entity.UserEntity

/**
 * Room database for CodeChecker
 */
@Database(
    entities = [
        UserEntity::class,
        AssignmentEntity::class,
        SubmissionEntity::class,
        ReportEntity::class,
        SimilarityEntity::class,
        AdminAuditLogEntity::class,
        AdminSettingEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Get User DAO
     */
    abstract fun userDao(): UserDao

    /**
     * Get Assignment DAO
     */
    abstract fun assignmentDao(): AssignmentDao

    /**
     * Get Submission DAO
     */
    abstract fun submissionDao(): SubmissionDao

    /**
     * Get Report DAO
     */
    abstract fun reportDao(): ReportDao

    /**
     * Get Similarity DAO
     */
    abstract fun similarityDao(): SimilarityDao

    /**
     * Get Admin Audit Log DAO
     */
    abstract fun adminAuditLogDao(): AdminAuditLogDao

    /**
     * Get Admin Setting DAO
     */
    abstract fun adminSettingDao(): AdminSettingDao
}

/**
 * Migration from version 1 to version 2
 * Adds admin management features:
 * - Add isActive and status columns to users table
 * - Create admin_audit_logs table
 * - Create admin_settings table
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add new columns to users table
        database.execSQL("ALTER TABLE users ADD COLUMN isActive INTEGER NOT NULL DEFAULT 1")
        database.execSQL("ALTER TABLE users ADD COLUMN status TEXT NOT NULL DEFAULT 'ACTIVE'")

        database.execSQL("ALTER TABLE assignments ADD COLUMN submission_limit INTEGER NOT NULL DEFAULT 200")
        database.execSQL("ALTER TABLE assignments ADD COLUMN python_version TEXT NOT NULL DEFAULT 'PYTHON3'")
        database.execSQL("ALTER TABLE assignments ADD COLUMN status TEXT NOT NULL DEFAULT 'ACTIVE'")

        // Create admin_audit_logs table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS admin_audit_logs (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                adminUserId INTEGER NOT NULL,
                action TEXT NOT NULL,
                targetType TEXT NOT NULL,
                targetId TEXT,
                timestamp INTEGER NOT NULL,
                result TEXT NOT NULL,
                details TEXT
            )
        """.trimIndent())

        // Create admin_settings table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS admin_settings (
                key TEXT PRIMARY KEY NOT NULL,
                value TEXT NOT NULL,
                type TEXT NOT NULL,
                updatedAt INTEGER NOT NULL,
                updatedBy INTEGER NOT NULL
            )
        """.trimIndent())

        // Initialize default admin settings
        val currentTime = System.currentTimeMillis()
        database.execSQL("""
            INSERT INTO admin_settings (key, value, type, updatedAt, updatedBy)
            VALUES ('similarity_threshold', '60', 'INT', $currentTime, 0)
        """.trimIndent())
        database.execSQL("""
            INSERT INTO admin_settings (key, value, type, updatedAt, updatedBy)
            VALUES ('report_retention_days', '30', 'INT', $currentTime, 0)
        """.trimIndent())
        database.execSQL("""
            INSERT INTO admin_settings (key, value, type, updatedAt, updatedBy)
            VALUES ('submission_retention_days', '180', 'INT', $currentTime, 0)
        """.trimIndent())
        database.execSQL("""
            INSERT INTO admin_settings (key, value, type, updatedAt, updatedBy)
            VALUES ('fast_compare_mode', 'false', 'BOOLEAN', $currentTime, 0)
        """.trimIndent())
        database.execSQL("""
            INSERT INTO admin_settings (key, value, type, updatedAt, updatedBy)
            VALUES ('log_level', 'INFO', 'STRING', $currentTime, 0)
        """.trimIndent())
        database.execSQL("""
            INSERT INTO admin_settings (key, value, type, updatedAt, updatedBy)
            VALUES ('auto_cleanup_enabled', 'false', 'BOOLEAN', $currentTime, 0)
        """.trimIndent())
        database.execSQL("""
            INSERT INTO admin_settings (key, value, type, updatedAt, updatedBy)
            VALUES ('max_submissions_per_assignment', '200', 'INT', $currentTime, 0)
        """.trimIndent())

        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS assignments_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                description TEXT NOT NULL,
                teacher_id INTEGER NOT NULL,
                due_date INTEGER,
                submission_limit INTEGER NOT NULL DEFAULT 200,
                python_version TEXT NOT NULL DEFAULT 'PYTHON3',
                status TEXT NOT NULL DEFAULT 'ACTIVE',
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
        database.execSQL(
            """
            INSERT INTO assignments_new (
                id, title, description, teacher_id, due_date, submission_limit, python_version, status, created_at
            )
            SELECT id, title, description, teacher_id, due_date, 200, 'PYTHON3', 'ACTIVE', created_at FROM assignments
            """.trimIndent()
        )
        database.execSQL("DROP TABLE assignments")
        database.execSQL("ALTER TABLE assignments_new RENAME TO assignments")
    }
}
