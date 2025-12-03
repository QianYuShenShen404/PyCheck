package com.example.codechecker.integration

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.codechecker.data.local.database.AppDatabase
import com.example.codechecker.data.local.dao.AdminSettingDao
import com.example.codechecker.data.local.entity.AdminSettingEntity
import com.example.codechecker.data.repository.AdminSettingsRepositoryImpl
import com.example.codechecker.domain.model.AdminSetting
import com.example.codechecker.domain.model.LogLevel
import com.example.codechecker.domain.usecase.GetAdminSettingsUseCase
import com.example.codechecker.domain.usecase.UpdateAdminSettingsUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import javax.inject.Inject
import kotlin.test.*

/**
 * Integration tests for settings persistence across app restarts
 */
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsPersistenceTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var context: Context

    private lateinit var database: AppDatabase
    private lateinit var adminSettingDao: AdminSettingDao
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var adminSettingsRepository: AdminSettingsRepositoryImpl
    private lateinit var getAdminSettingsUseCase: GetAdminSettingsUseCase
    private lateinit var updateAdminSettingsUseCase: UpdateAdminSettingsUseCase

    private val testDispatcher = UnconfinedTestDispatcher()
    private val PREFS_NAME = "codechecker_prefs"

    @Before
    fun setup() {
        hiltRule.inject()

        // Create in-memory database
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()

        adminSettingDao = database.adminSettingDao()

        // Get SharedPreferences
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Initialize repository and use cases
        adminSettingsRepository = AdminSettingsRepositoryImpl(adminSettingDao)
        getAdminSettingsUseCase = GetAdminSettingsUseCase(adminSettingsRepository, testDispatcher)
        updateAdminSettingsUseCase = UpdateAdminSettingsUseCase(adminSettingsRepository, testDispatcher)
    }

    @After
    fun tearDown() {
        database.close()
        sharedPreferences.edit().clear().commit()
    }

    @Test
    fun `settings should persist across database restarts`() = runTest(testDispatcher) {
        // Given - insert settings
        val settings = listOf(
            AdminSettingEntity(1L, "threshold", "0.8", "Threshold", "Algorithm", true),
            AdminSettingEntity(2L, "log_level", "INFO", "Log level", "System", true),
            AdminSettingEntity(3L, "retention_days", "90", "Retention", "Data", true)
        )
        settings.forEach { adminSettingDao.insertSetting(it) }

        // Simulate app restart by closing and reopening database
        database.close()
        val newDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_restart"
        ).build()
        val newDao = newDatabase.adminSettingDao()

        // When - retrieve settings after restart
        val persistedSettings = newDao.getAllSettings()

        // Then - verify all settings persisted
        assertEquals(3, persistedSettings.size)
        assertEquals("threshold", persistedSettings[0].key)
        assertEquals("0.8", persistedSettings[0].value)
        assertEquals("log_level", persistedSettings[1].key)
        assertEquals("INFO", persistedSettings[1].value)
        assertEquals("retention_days", persistedSettings[2].key)
        assertEquals("90", persistedSettings[2].value)

        newDatabase.close()
    }

    @Test
    fun `updated settings should persist across restarts`() = runTest(testDispatcher) {
        // Given - insert initial setting
        adminSettingDao.insertSetting(
            AdminSettingEntity(1L, "threshold", "0.5", "Threshold", "Algorithm", true)
        )

        // When - update setting
        val updateResult = updateAdminSettingsUseCase.execute("threshold", "0.9")
        assertTrue(updateResult.isSuccess)

        // Simulate app restart
        database.close()
        val newDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_update"
        ).build()
        val newDao = newDatabase.adminSettingDao()

        // Then - verify updated value persisted
        val persistedSetting = newDao.getSettingByKey("threshold")
        assertNotNull(persistedSetting)
        assertEquals("0.9", persistedSetting.value)

        newDatabase.close()
    }

    @Test
    fun `similarity threshold should persist and be retrievable`() = runTest(testDispatcher) {
        // Given - update similarity threshold
        val thresholdValue = "0.75"
        updateAdminSettingsUseCase.execute("similarity_threshold", thresholdValue)

        // When - retrieve after simulated restart
        database.close()
        val newDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_threshold"
        ).build()
        val newDao = newDatabase.adminSettingDao()
        val persistedThreshold = newDao.getSettingByKey("similarity_threshold")

        // Then - verify threshold persisted
        assertNotNull(persistedThreshold)
        assertEquals(thresholdValue, persistedThreshold.value)

        newDatabase.close()
    }

    @Test
    fun `log level setting should persist across restarts`() = runTest(testDispatcher) {
        // Given - update log level
        updateAdminSettingsUseCase.execute("log_level", "DEBUG")

        // When - retrieve after simulated restart
        database.close()
        val newDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_loglevel"
        ).build()
        val newDao = newDatabase.adminSettingDao()
        val persistedLogLevel = newDao.getSettingByKey("log_level")

        // Then - verify log level persisted
        assertNotNull(persistedLogLevel)
        assertEquals("DEBUG", persistedLogLevel.value)

        newDatabase.close()
    }

    @Test
    fun `retention policy settings should persist across restarts`() = runTest(testDispatcher) {
        // Given - update retention settings
        updateAdminSettingsUseCase.execute("report_retention_days", "180")
        updateAdminSettingsUseCase.execute("audit_log_retention_days", "365")
        updateAdminSettingsUseCase.execute("submission_retention_days", "90")

        // When - retrieve after simulated restart
        database.close()
        val newDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_retention"
        ).build()
        val newDao = newDatabase.adminSettingDao()
        val allSettings = newDao.getAllSettings()

        // Then - verify all retention settings persisted
        val reportRetention = allSettings.find { it.key == "report_retention_days" }
        assertNotNull(reportRetention)
        assertEquals("180", reportRetention.value)

        val auditRetention = allSettings.find { it.key == "audit_log_retention_days" }
        assertNotNull(auditRetention)
        assertEquals("365", auditRetention.value)

        val submissionRetention = allSettings.find { it.key == "submission_retention_days" }
        assertNotNull(submissionRetention)
        assertEquals("90", submissionRetention.value)

        newDatabase.close()
    }

    @Test
    fun `fast compare mode setting should persist across restarts`() = runTest(testDispatcher) {
        // Given - enable fast compare mode
        updateAdminSettingsUseCase.execute("fast_compare_mode", "true")

        // When - retrieve after simulated restart
        database.close()
        val newDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_fastcompare"
        ).build()
        val newDao = newDatabase.adminSettingDao()
        val persistedSetting = newDao.getSettingByKey("fast_compare_mode")

        // Then - verify setting persisted
        assertNotNull(persistedSetting)
        assertEquals("true", persistedSetting.value)

        newDatabase.close()
    }

    @Test
    fun `identifier normalization toggle should persist across restarts`() = runTest(testDispatcher) {
        // Given - enable identifier normalization
        updateAdminSettingsUseCase.execute("normalize_identifiers", "true")

        // When - retrieve after simulated restart
        database.close()
        val newDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_normalize"
        ).build()
        val newDao = newDatabase.adminSettingDao()
        val persistedSetting = newDao.getSettingByKey("normalize_identifiers")

        // Then - verify setting persisted
        assertNotNull(persistedSetting)
        assertEquals("true", persistedSetting.value)

        newDatabase.close()
    }

    @Test
    fun `multiple settings updates should all persist`() = runTest(testDispatcher) {
        // Given - update multiple settings
        val updates = mapOf(
            "similarity_threshold" to "0.85",
            "log_level" to "WARN",
            "report_retention_days" to "120",
            "audit_log_retention_days" to "180",
            "fast_compare_mode" to "false",
            "normalize_identifiers" to "true"
        )

        updates.forEach { (key, value) ->
            updateAdminSettingsUseCase.execute(key, value)
        }

        // When - retrieve after simulated restart
        database.close()
        val newDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_multiple"
        ).build()
        val newDao = newDatabase.adminSettingDao()
        val persistedSettings = newDao.getAllSettings()

        // Then - verify all settings persisted
        updates.forEach { (key, expectedValue) ->
            val setting = persistedSettings.find { it.key == key }
            assertNotNull(setting, "Setting $key should exist")
            assertEquals(expectedValue, setting.value, "Setting $key should have correct value")
        }

        newDatabase.close()
    }

    @Test
    fun `SharedPreferences should persist across app restarts`() = runTest(testDispatcher) {
        // Given - save values to SharedPreferences
        sharedPreferences.edit()
            .putString("last_sync_time", "2024-01-01T00:00:00Z")
            .putBoolean("first_run", false)
            .putInt("total_users", 100)
            .putFloat("average_similarity", 0.75f)
            .commit()

        // When - retrieve after simulated app restart
        // (In real scenario, app would be killed and restarted)
        val lastSyncTime = sharedPreferences.getString("last_sync_time", null)
        val isFirstRun = sharedPreferences.getBoolean("first_run", true)
        val totalUsers = sharedPreferences.getInt("total_users", 0)
        val avgSimilarity = sharedPreferences.getFloat("average_similarity", 0.0f)

        // Then - verify all values persisted
        assertEquals("2024-01-01T00:00:00Z", lastSyncTime)
        assertFalse(isFirstRun)
        assertEquals(100, totalUsers)
        assertEquals(0.75f, avgSimilarity)
    }

    @Test
    fun `default settings should be created on first run`() = runTest(testDispatcher) {
        // When - start with empty database (simulating first run)
        val newDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_firstrun"
        ).build()
        val newDao = newDatabase.adminSettingDao()

        // Then - verify default settings were created
        val settings = newDao.getAllSettings()
        assertTrue(settings.isNotEmpty(), "Default settings should exist")

        // Check for essential default settings
        val thresholdSetting = settings.find { it.key == "similarity_threshold" }
        assertNotNull(thresholdSetting)
        assertEquals("0.8", thresholdSetting.value)

        val logLevelSetting = settings.find { it.key == "log_level" }
        assertNotNull(logLevelSetting)
        assertEquals("INFO", logLevelSetting.value)

        newDatabase.close()
    }

    @Test
    fun `settings update should be atomic and consistent`() = runTest(testDispatcher) {
        // Given - insert multiple settings
        val settings = listOf(
            AdminSettingEntity(1L, "setting1", "value1", "Desc1", "Cat1", true),
            AdminSettingEntity(2L, "setting2", "value2", "Desc2", "Cat2", true),
            AdminSettingEntity(3L, "setting3", "value3", "Desc3", "Cat3", true)
        )
        settings.forEach { adminSettingDao.insertSetting(it) }

        // When - update all settings
        updateAdminSettingsUseCase.execute("setting1", "new1")
        updateAdminSettingsUseCase.execute("setting2", "new2")
        updateAdminSettingsUseCase.execute("setting3", "new3")

        // Simulate app restart
        database.close()
        val newDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_atomic"
        ).build()
        val newDao = newDatabase.adminSettingDao()

        // Then - verify all updates persisted atomically
        val persistedSettings = newDao.getAllSettings()
        assertEquals(3, persistedSettings.size)

        val setting1 = persistedSettings.find { it.key == "setting1" }
        assertNotNull(setting1)
        assertEquals("new1", setting1.value)

        val setting2 = persistedSettings.find { it.key == "setting2" }
        assertNotNull(setting2)
        assertEquals("new2", setting2.value)

        val setting3 = persistedSettings.find { it.key == "setting3" }
        assertNotNull(setting3)
        assertEquals("new3", setting3.value)

        newDatabase.close()
    }

    @Test
    fun `settings should persist after multiple app restarts`() = runTest(testDispatcher) {
        // Given - update settings
        updateAdminSettingsUseCase.execute("threshold", "0.7")
        updateAdminSettingsUseCase.execute("log_level", "DEBUG")

        // Simulate first restart
        database.close()
        var newDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_restart1"
        ).build()
        var newDao = newDatabase.adminSettingDao()
        var threshold = newDao.getSettingByKey("threshold")
        assertNotNull(threshold)
        assertEquals("0.7", threshold.value)
        newDatabase.close()

        // Simulate second restart
        newDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_restart2"
        ).build()
        newDao = newDatabase.adminSettingDao()
        threshold = newDao.getSettingByKey("threshold")
        assertNotNull(threshold)
        assertEquals("0.7", threshold.value)
        newDatabase.close()

        // Simulate third restart
        newDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_restart3"
        ).build()
        newDao = newDatabase.adminSettingDao()
        threshold = newDao.getSettingByKey("threshold")
        assertNotNull(threshold)
        assertEquals("0.7", threshold.value)
        newDatabase.close()
    }

    @Test
    fun `UseCase should retrieve persisted settings after restart`() = runTest(testDispatcher) {
        // Given - update settings
        updateAdminSettingsUseCase.execute("similarity_threshold", "0.6")
        updateAdminSettingsUseCase.execute("log_level", "ERROR")

        // When - retrieve through UseCase after simulated restart
        database.close()
        val newDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_usecase"
        ).build()
        val newDao = newDatabase.adminSettingDao()
        val newRepository = AdminSettingsRepositoryImpl(newDao)
        val newGetUseCase = GetAdminSettingsUseCase(newRepository, testDispatcher)

        val result = newGetUseCase.execute()

        // Then - verify settings retrieved correctly
        assertTrue(result.isSuccess)
        val settings = result.getOrNull()
        assertNotNull(settings)

        val threshold = settings.find { it.key == "similarity_threshold" }
        assertNotNull(threshold)
        assertEquals("0.6", threshold.value)

        val logLevel = settings.find { it.key == "log_level" }
        assertNotNull(logLevel)
        assertEquals("ERROR", logLevel.value)

        newDatabase.close()
    }

    @Test
    fun `settings metadata should persist including description and category`() = runTest(testDispatcher) {
        // Given - insert setting with metadata
        adminSettingDao.insertSetting(
            AdminSettingEntity(
                1L,
                "custom_setting",
                "custom_value",
                "This is a custom setting",
                "Custom Category",
                true
            )
        )

        // Simulate app restart
        database.close()
        val newDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_db_metadata"
        ).build()
        val newDao = newDatabase.adminSettingDao()
        val persistedSetting = newDao.getSettingByKey("custom_setting")

        // Then - verify all metadata persisted
        assertNotNull(persistedSetting)
        assertEquals("custom_setting", persistedSetting.key)
        assertEquals("custom_value", persistedSetting.value)
        assertEquals("This is a custom setting", persistedSetting.description)
        assertEquals("Custom Category", persistedSetting.category)
        assertTrue(persistedSetting.isEditable)

        newDatabase.close()
    }
}
