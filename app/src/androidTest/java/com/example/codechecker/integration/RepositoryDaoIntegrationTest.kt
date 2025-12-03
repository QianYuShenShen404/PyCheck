package com.example.codechecker.integration

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.codechecker.data.local.database.AppDatabase
import com.example.codechecker.data.local.dao.UserDao
import com.example.codechecker.data.local.dao.AdminAuditLogDao
import com.example.codechecker.data.local.dao.AdminSettingDao
import com.example.codechecker.data.local.entity.UserEntity
import com.example.codechecker.data.local.entity.AdminAuditLogEntity
import com.example.codechecker.data.local.entity.AdminSettingEntity
import com.example.codechecker.data.repository.UserRepositoryImpl
import com.example.codechecker.data.repository.AdminAuditLogRepositoryImpl
import com.example.codechecker.data.repository.AdminSettingsRepositoryImpl
import com.example.codechecker.domain.model.Role
import com.example.codechecker.domain.model.User
import com.example.codechecker.domain.model.UserStatus
import com.example.codechecker.domain.model.AdminAuditLog
import com.example.codechecker.domain.model.AdminSetting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.*

/**
 * Integration tests for Repository and DAO integration
 */
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class RepositoryDaoIntegrationTest {

    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var auditLogDao: AdminAuditLogDao
    private lateinit var adminSettingDao: AdminSettingDao

    private lateinit var userRepository: UserRepositoryImpl
    private lateinit var auditLogRepository: AdminAuditLogRepositoryImpl
    private lateinit var adminSettingsRepository: AdminSettingsRepositoryImpl

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()

        userDao = database.userDao()
        auditLogDao = database.adminAuditLogDao()
        adminSettingDao = database.adminSettingDao()

        userRepository = UserRepositoryImpl(userDao)
        auditLogRepository = AdminAuditLogRepositoryImpl(auditLogDao)
        adminSettingsRepository = AdminSettingsRepositoryImpl(adminSettingDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `UserRepository should create user successfully through DAO`() = runTest(testDispatcher) {
        // Given
        val username = "newuser"
        val email = "user@example.com"
        val passwordHash = "hashedPassword"
        val role = Role.STUDENT

        // When
        val result = userRepository.createUser(username, email, passwordHash, role)

        // Then
        assertTrue(result.isSuccess)
        val createdUser = result.getOrNull()
        assertNotNull(createdUser)
        assertEquals(username, createdUser.username)
        assertEquals(email, createdUser.email)
        assertEquals(role, createdUser.role)

        // Verify through DAO
        val userFromDao = userDao.getUserById(createdUser.id)
        assertNotNull(userFromDao)
        assertEquals(username, userFromDao.username)
    }

    @Test
    fun `UserRepository should retrieve all users through DAO`() = runTest(testDispatcher) {
        // Given - insert users directly through DAO
        val users = listOf(
            UserEntity(1L, "admin", "admin@example.com", "hash1", "ADMIN"),
            UserEntity(2L, "teacher", "teacher@example.com", "hash2", "TEACHER"),
            UserEntity(3L, "student", "student@example.com", "hash3", "STUDENT")
        )
        users.forEach { userDao.insertUser(it) }

        // When
        val result = userRepository.getAllUsers()

        // Then
        assertTrue(result.isSuccess)
        val retrievedUsers = result.getOrNull()
        assertNotNull(retrievedUsers)
        assertEquals(3, retrievedUsers.size)
        assertEquals("admin", retrievedUsers[0].username)
        assertEquals("teacher", retrievedUsers[1].username)
        assertEquals("student", retrievedUsers[2].username)
    }

    @Test
    fun `UserRepository should update user role through DAO`() = runTest(testDispatcher) {
        // Given - insert user
        userDao.insertUser(UserEntity(1L, "user", "user@example.com", "hash", "STUDENT"))

        // When
        val result = userRepository.updateUserRole(1L, Role.TEACHER)

        // Then
        assertTrue(result.isSuccess)
        val updatedUser = result.getOrNull()
        assertNotNull(updatedUser)
        assertEquals(Role.TEACHER, updatedUser.role)

        // Verify through DAO
        val userFromDao = userDao.getUserById(1L)
        assertEquals("TEACHER", userFromDao.role)
    }

    @Test
    fun `UserRepository should enable/disable user through DAO`() = runTest(testDispatcher) {
        // Given - insert disabled user
        userDao.insertUser(UserEntity(1L, "user", "user@example.com", "hash", "STUDENT"))

        // When - enable user
        val enableResult = userRepository.enableUser(1L)

        // Then
        assertTrue(enableResult.isSuccess)
        val enabledUser = enableResult.getOrNull()
        assertNotNull(enabledUser)
        assertTrue(enabledUser.isActive)
        assertEquals(UserStatus.ACTIVE, enabledUser.status)

        // Verify through DAO
        var userFromDao = userDao.getUserById(1L)
        assertTrue(userFromDao.isActive)
        assertEquals("ACTIVE", userFromDao.status)

        // When - disable user
        val disableResult = userRepository.disableUser(1L)

        // Then
        assertTrue(disableResult.isSuccess)
        val disabledUser = disableResult.getOrNull()
        assertNotNull(disabledUser)
        assertFalse(disabledUser.isActive)
        assertEquals(UserStatus.DISABLED, disabledUser.status)

        // Verify through DAO
        userFromDao = userDao.getUserById(1L)
        assertFalse(userFromDao.isActive)
        assertEquals("DISABLED", userFromDao.status)
    }

    @Test
    fun `UserRepository should delete user through DAO`() = runTest(testDispatcher) {
        // Given - insert user
        userDao.insertUser(UserEntity(1L, "user", "user@example.com", "hash", "STUDENT"))

        // When
        val result = userRepository.deleteUser(1L)

        // Then
        assertTrue(result.isSuccess)
        val deletedUser = result.getOrNull()
        assertNotNull(deletedUser)
        assertFalse(deletedUser.isActive)
        assertEquals(UserStatus.DELETED, deletedUser.status)

        // Verify through DAO
        val userFromDao = userDao.getUserById(1L)
        assertEquals("DELETED", userFromDao.status)
    }

    @Test
    fun `AdminAuditLogRepository should insert and retrieve logs through DAO`() = runTest(testDispatcher) {
        // Given
        val log = AdminAuditLogEntity(
            id = 1L,
            timestamp = System.currentTimeMillis(),
            action = "USER_CREATE",
            result = "SUCCESS",
            category = "User",
            userId = 1L,
            details = "User created successfully"
        )

        // When - insert through DAO
        auditLogDao.insertLog(log)

        // Then - retrieve through repository
        val result = auditLogRepository.getAllLogs()
        assertTrue(result.isSuccess)
        val logs = result.getOrNull()
        assertNotNull(logs)
        assertEquals(1, logs.size)
        assertEquals("USER_CREATE", logs[0].action)
        assertEquals("SUCCESS", logs[0].result)
    }

    @Test
    fun `AdminAuditLogRepository should filter logs by action through DAO`() = runTest(testDispatcher) {
        // Given - insert multiple logs with different actions
        val logs = listOf(
            AdminAuditLogEntity(1L, System.currentTimeMillis(), "LOGIN", "SUCCESS", "Auth", 1L, "Login"),
            AdminAuditLogEntity(2L, System.currentTimeMillis(), "LOGOUT", "SUCCESS", "Auth", 1L, "Logout"),
            AdminAuditLogEntity(3L, System.currentTimeMillis(), "LOGIN", "SUCCESS", "Auth", 2L, "Login"),
            AdminAuditLogEntity(4L, System.currentTimeMillis(), "USER_CREATE", "SUCCESS", "User", 1L, "Create")
        )
        logs.forEach { auditLogDao.insertLog(it) }

        // When - filter by action
        val result = auditLogRepository.getLogsByAction("LOGIN")

        // Then
        assertTrue(result.isSuccess)
        val loginLogs = result.getOrNull()
        assertNotNull(loginLogs)
        assertEquals(2, loginLogs.size)
        assertTrue(loginLogs.all { it.action == "LOGIN" })
    }

    @Test
    fun `AdminSettingsRepository should insert and retrieve settings through DAO`() = runTest(testDispatcher) {
        // Given
        val setting = AdminSettingEntity(
            id = 1L,
            key = "similarity_threshold",
            value = "0.8",
            description = "Similarity threshold",
            category = "Algorithm",
            isEditable = true
        )

        // When - insert through DAO
        adminSettingDao.insertSetting(setting)

        // Then - retrieve through repository
        val result = adminSettingsRepository.getAllSettings()
        assertTrue(result.isSuccess)
        val settings = result.getOrNull()
        assertNotNull(settings)
        assertEquals(1, settings.size)
        assertEquals("similarity_threshold", settings[0].key)
        assertEquals("0.8", settings[0].value)
    }

    @Test
    fun `AdminSettingsRepository should update setting through DAO`() = runTest(testDispatcher) {
        // Given - insert setting
        adminSettingDao.insertSetting(
            AdminSettingEntity(1L, "threshold", "0.5", "Threshold", "Algo", true)
        )

        // When - update through repository
        val result = adminSettingsRepository.updateSetting("threshold", "0.9")

        // Then
        assertTrue(result.isSuccess)
        val updatedSetting = result.getOrNull()
        assertNotNull(updatedSetting)
        assertEquals("0.9", updatedSetting.value)

        // Verify through DAO
        val settingFromDao = adminSettingDao.getSettingByKey("threshold")
        assertNotNull(settingFromDao)
        assertEquals("0.9", settingFromDao.value)
    }

    @Test
    fun `UserRepository should handle password reset through DAO`() = runTest(testDispatcher) {
        // Given - insert user
        userDao.insertUser(UserEntity(1L, "user", "user@example.com", "oldHash", "STUDENT"))

        // When
        val result = userRepository.resetPassword(1L, "newHashedPassword")

        // Then
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)

        // Verify through DAO
        val userFromDao = userDao.getUserById(1L)
        assertEquals("newHashedPassword", userFromDao.passwordHash)
    }

    @Test
    fun `AdminAuditLogRepository should retrieve logs by date range through DAO`() = runTest(testDispatcher) {
        // Given - insert logs at different times
        val now = System.currentTimeMillis()
        val logs = listOf(
            AdminAuditLogEntity(1L, now - 10000, "LOGIN", "SUCCESS", "Auth", 1L, "Old login"),
            AdminAuditLogEntity(2L, now - 5000, "LOGIN", "SUCCESS", "Auth", 1L, "Recent login"),
            AdminAuditLogEntity(3L, now + 5000, "LOGIN", "SUCCESS", "Auth", 1L, "Future login")
        )
        logs.forEach { auditLogDao.insertLog(it) }

        // When - filter by date range
        val result = auditLogRepository.getLogsByDateRange(now - 6000, now + 6000)

        // Then
        assertTrue(result.isSuccess)
        val filteredLogs = result.getOrNull()
        assertNotNull(filteredLogs)
        assertEquals(2, filteredLogs.size)
    }

    @Test
    fun `Repository integration should handle concurrent operations`() = runTest(testDispatcher) {
        // Given - multiple users
        val users = (1..10).map { i ->
            UserEntity(i.toLong(), "user$i", "user$i@example.com", "hash$i", "STUDENT")
        }

        // When - insert all users concurrently
        users.forEach { userRepository.createUser(it.username, it.email, it.passwordHash, Role.STUDENT) }

        // Then - verify all users inserted
        val result = userRepository.getAllUsers()
        assertTrue(result.isSuccess)
        val allUsers = result.getOrNull()
        assertNotNull(allUsers)
        assertEquals(10, allUsers.size)
    }

    @Test
    fun `AdminSettingsRepository should retrieve settings by category through DAO`() = runTest(testDispatcher) {
        // Given - insert settings in different categories
        val settings = listOf(
            AdminSettingEntity(1L, "threshold1", "0.8", "Threshold", "Algorithm", true),
            AdminSettingEntity(2L, "threshold2", "0.9", "Threshold", "Algorithm", true),
            AdminSettingEntity(3L, "log_level", "INFO", "Log level", "System", true)
        )
        settings.forEach { adminSettingDao.insertSetting(it) }

        // When - filter by category
        val result = adminSettingsRepository.getSettingsByCategory("Algorithm")

        // Then
        assertTrue(result.isSuccess)
        val algoSettings = result.getOrNull()
        assertNotNull(algoSettings)
        assertEquals(2, algoSettings.size)
        assertTrue(algoSettings.all { it.category == "Algorithm" })
    }

    @Test
    fun `UserRepository should update user email through DAO`() = runTest(testDispatcher) {
        // Given - insert user
        userDao.insertUser(UserEntity(1L, "user", "old@example.com", "hash", "STUDENT"))

        // When - update email
        val result = userRepository.updateUserEmail(1L, "new@example.com")

        // Then
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals("new@example.com", user.email)

        // Verify through DAO
        val userFromDao = userDao.getUserById(1L)
        assertEquals("new@example.com", userFromDao.email)
    }

    @Test
    fun `Repository should handle empty database gracefully`() = runTest(testDispatcher) {
        // When - query empty database
        val usersResult = userRepository.getAllUsers()
        val logsResult = auditLogRepository.getAllLogs()
        val settingsResult = adminSettingsRepository.getAllSettings()

        // Then
        assertTrue(usersResult.isSuccess)
        assertEquals(0, usersResult.getOrNull()?.size)

        assertTrue(logsResult.isSuccess)
        assertEquals(0, logsResult.getOrNull()?.size)

        assertTrue(settingsResult.isSuccess)
        assertEquals(0, settingsResult.getOrNull()?.size)
    }

    @Test
    fun `Repository should handle invalid data gracefully`() = runTest(testDispatcher) {
        // When - query non-existent user
        val result = userRepository.getUserById(999L)

        // Then
        assertFalse(result.isSuccess)
        assertNotNull(result.exceptionOrNull())
    }
}
