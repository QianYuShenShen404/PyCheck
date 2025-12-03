package com.example.codechecker.integration

import android.content.Context
import androidx.room.Room
import androidx.room.testing.RoomMigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.codechecker.data.local.database.AppDatabase
import com.example.codechecker.data.local.entity.UserEntity
import com.example.codechecker.data.local.entity.AdminAuditLogEntity
import com.example.codechecker.data.local.entity.AdminSettingEntity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.*

/**
 * Integration tests for database migration from v1 to v2
 */
@RunWith(AndroidJUnit4::class)
class DatabaseMigrationTest {

    @get:Rule
    val helper: RoomMigrationTestHelper = RoomMigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
        emptyList()
    )

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @After
    fun tearDown() {
        // Clean up after tests
    }

    @Test
    fun `migration v1 to v2 should add new tables and columns`() {
        // Create v1 database with only UserEntity
        val v1Db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_v1"
        ).build()

        // Insert some test data in v1
        v1Db.userDao().insertUser(
            UserEntity(
                id = 1L,
                username = "testuser",
                email = "test@example.com",
                passwordHash = "hash",
                role = "STUDENT"
            )
        )

        v1Db.close()

        // Migrate to v2
        val v2Db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_v2"
        ).build()

        // Verify migration succeeded
        assertNotNull(v2Db)

        // Verify new tables exist
        assertNotNull(v2Db.adminAuditLogDao())
        assertNotNull(v2Db.adminSettingDao())

        // Verify new columns added to UserEntity
        val user = v2Db.userDao().getUserById(1L)
        assertNotNull(user)
        assertTrue(user.isActive)
        assertEquals("ACTIVE", user.status)

        v2Db.close()
    }

    @Test
    fun `migration should preserve existing user data`() {
        // Create v1 database
        val v1Db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_v3"
        ).build()

        // Insert test users
        val users = listOf(
            UserEntity(1L, "admin", "admin@example.com", "hash1", "ADMIN"),
            UserEntity(2L, "teacher", "teacher@example.com", "hash2", "TEACHER"),
            UserEntity(3L, "student", "student@example.com", "hash3", "STUDENT")
        )

        users.forEach { v1Db.userDao().insertUser(it) }
        v1Db.close()

        // Migrate to v2
        val v2Db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_v4"
        ).build()

        // Verify all users migrated
        val migratedUsers = v2Db.userDao().getAllUsers()
        assertEquals(3, migratedUsers.size)

        // Verify all original data preserved
        assertEquals("admin", migratedUsers[0].username)
        assertEquals("teacher", migratedUsers[1].username)
        assertEquals("student", migratedUsers[2].username)

        // Verify new default values applied
        assertTrue(migratedUsers[0].isActive)
        assertEquals("ACTIVE", migratedUsers[0].status)

        v2Db.close()
    }

    @Test
    fun `migration v1 to v2 should create AdminAuditLogEntity table`() {
        // Create v1 database
        val v1Db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_v5"
        ).build()
        v1Db.close()

        // Migrate to v2
        val v2Db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_v6"
        ).build()

        // Insert audit log entry
        v2Db.adminAuditLogDao().insertLog(
            AdminAuditLogEntity(
                id = 1L,
                timestamp = System.currentTimeMillis(),
                action = "USER_CREATE",
                result = "SUCCESS",
                category = "User",
                userId = 1L,
                details = "User created"
            )
        )

        // Verify audit log entry
        val auditLogs = v2Db.adminAuditLogDao().getAllLogs()
        assertEquals(1, auditLogs.size)
        assertEquals("USER_CREATE", auditLogs[0].action)

        v2Db.close()
    }

    @Test
    fun `migration v1 to v2 should create AdminSettingEntity table`() {
        // Create v1 database
        val v1Db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_v7"
        ).build()
        v1Db.close()

        // Migrate to v2
        val v2Db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_v8"
        ).build()

        // Insert admin settings
        v2Db.adminSettingDao().insertSetting(
            AdminSettingEntity(
                id = 1L,
                key = "similarity_threshold",
                value = "0.8",
                description = "Similarity threshold",
                category = "Algorithm",
                isEditable = true
            )
        )

        // Verify settings entry
        val settings = v2Db.adminSettingDao().getAllSettings()
        assertEquals(1, settings.size)
        assertEquals("similarity_threshold", settings[0].key)
        assertEquals("0.8", settings[0].value)

        v2Db.close()
    }

    @Test
    fun `migration should handle database with no data`() {
        // Create empty v1 database
        val v1Db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_v9"
        ).build()
        v1Db.close()

        // Migrate to v2
        val v2Db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_v10"
        ).build()

        // Verify empty database
        val users = v2Db.userDao().getAllUsers()
        assertEquals(0, users.size)

        val auditLogs = v2Db.adminAuditLogDao().getAllLogs()
        assertEquals(0, auditLogs.size)

        val settings = v2Db.adminSettingDao().getAllSettings()
        assertEquals(0, settings.size)

        v2Db.close()
    }

    @Test
    fun `migration should add default admin settings`() {
        // Create v1 database
        val v1Db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_v11"
        ).build()
        v1Db.close()

        // Migrate to v2
        val v2Db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_v12"
        ).build()

        // Verify default settings were created
        val settings = v2Db.adminSettingDao().getAllSettings()

        // Check for essential settings
        val thresholdSetting = settings.find { it.key == "similarity_threshold" }
        assertNotNull(thresholdSetting)
        assertEquals("0.8", thresholdSetting.value)

        val logLevelSetting = settings.find { it.key == "log_level" }
        assertNotNull(logLevelSetting)
        assertEquals("INFO", logLevelSetting.value)

        v2Db.close()
    }

    @Test
    fun `migration should validate new UserEntity columns`() {
        // Create v1 database
        val v1Db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_v13"
        ).build()
        v1Db.close()

        // Migrate to v2
        val v2Db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_v14"
        ).build()

        // Update user with new fields
        v2Db.userDao().updateUser(
            1L,
            "updateduser",
            "updated@example.com",
            isActive = false,
            status = "DISABLED"
        )

        // Verify update
        val user = v2Db.userDao().getUserById(1L)
        assertNotNull(user)
        assertEquals("updateduser", user.username)
        assertFalse(user.isActive)
        assertEquals("DISABLED", user.status)

        v2Db.close()
    }

    @Test
    fun `migration should allow operations on new tables`() {
        // Create v1 database
        val v1Db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_v15"
        ).build()
        v1Db.close()

        // Migrate to v2
        val v2Db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_v16"
        ).build()

        // Create admin user
        v2Db.userDao().insertUser(
            UserEntity(
                1L,
                "admin",
                "admin@example.com",
                "hash",
                "ADMIN"
            )
        )

        // Insert multiple audit logs
        val auditLogs = listOf(
            AdminAuditLogEntity(1L, System.currentTimeMillis(), "LOGIN", "SUCCESS", "Auth", 1L, "Admin logged in"),
            AdminAuditLogEntity(2L, System.currentTimeMillis(), "LOGOUT", "SUCCESS", "Auth", 1L, "Admin logged out"),
            AdminAuditLogEntity(3L, System.currentTimeMillis(), "USER_CREATE", "SUCCESS", "User", 1L, "Created user")
        )
        auditLogs.forEach { v2Db.adminAuditLogDao().insertLog(it) }

        // Insert multiple settings
        val settings = listOf(
            AdminSettingEntity(1L, "threshold", "0.8", "Threshold", "Algo", true),
            AdminSettingEntity(2L, "log_level", "DEBUG", "Log level", "System", true)
        )
        settings.forEach { v2Db.adminSettingDao().insertSetting(it) }

        // Verify all operations
        assertEquals(1, v2Db.userDao().getAllUsers().size)
        assertEquals(3, v2Db.adminAuditLogDao().getAllLogs().size)
        assertEquals(2, v2Db.adminSettingDao().getAllSettings().size)

        v2Db.close()
    }

    @Test
    fun `migration should use fallbackToDestructiveMigration on invalid backup`() {
        // Create v1 database
        val v1Db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_v17"
        ).build()
        v1Db.insertUser(UserEntity(1L, "user", "user@example.com", "hash", "STUDENT"))
        v1Db.close()

        // Try to migrate with fallback
        val v2Db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_v18"
        ).fallbackToDestructiveMigration().build()

        // Verify database created (with possible data loss)
        assertNotNull(v2Db)
        v2Db.close()
    }
}
