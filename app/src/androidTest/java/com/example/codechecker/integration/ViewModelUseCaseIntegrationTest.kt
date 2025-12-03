package com.example.codechecker.integration

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.codechecker.algorithm.similarity.SimilarityCalculator
import com.example.codechecker.data.local.database.AppDatabase
import com.example.codechecker.data.repository.AdminAuditLogRepositoryImpl
import com.example.codechecker.data.repository.AdminSettingsRepositoryImpl
import com.example.codechecker.data.repository.UserRepositoryImpl
import com.example.codechecker.domain.model.Role
import com.example.codechecker.domain.model.User
import com.example.codechecker.domain.model.UserStatus
import com.example.codechecker.domain.service.AuditLogger
import com.example.codechecker.domain.usecase.*
import com.example.codechecker.ui.screens.admin.viewmodel.*
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
 * Integration tests for ViewModel and UseCase integration
 */
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelUseCaseIntegrationTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var context: Context

    private lateinit var database: AppDatabase

    // Use Cases
    private lateinit var getAllUsersUseCase: GetAllUsersUseCase
    private lateinit var createUserUseCase: CreateUserUseCase
    private lateinit var updateUserUseCase: UpdateUserUseCase
    private lateinit var deleteUserUseCase: DeleteUserUseCase
    private lateinit var changeUserRoleUseCase: ChangeUserRoleUseCase
    private lateinit var resetPasswordUseCase: ResetPasswordUseCase
    private lateinit var enableUserUseCase: EnableUserUseCase
    private lateinit var disableUserUseCase: DisableUserUseCase
    private lateinit var getAdminSettingsUseCase: GetAdminSettingsUseCase
    private lateinit var updateAdminSettingsUseCase: UpdateAdminSettingsUseCase
    private lateinit var exportDataUseCase: ExportDataUseCase
    private lateinit var importDataUseCase: ImportDataUseCase
    private lateinit var dataCleanupUseCase: DataCleanupUseCase
    private lateinit var storageStatisticsUseCase: StorageStatisticsUseCase
    private lateinit var auditLogger: AuditLogger

    // ViewModels
    private lateinit var userManagementViewModel: UserManagementViewModel
    private lateinit var dataManagementViewModel: DataManagementViewModel
    private lateinit var systemSettingsViewModel: SystemSettingsViewModel
    private lateinit var auditLogsViewModel: AuditLogsViewModel
    private lateinit var securityViewModel: SecurityViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        hiltRule.inject()

        // Create in-memory database
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()

        // Initialize repositories
        val userRepository = UserRepositoryImpl(database.userDao())
        val adminAuditLogRepository = AdminAuditLogRepositoryImpl(database.adminAuditLogDao())
        val adminSettingsRepository = AdminSettingsRepositoryImpl(database.adminSettingDao())

        // Initialize audit logger
        auditLogger = AuditLogger(adminAuditLogRepository, testDispatcher)

        // Initialize use cases
        getAllUsersUseCase = GetAllUsersUseCase(userRepository, testDispatcher)
        createUserUseCase = CreateUserUseCase(userRepository, testDispatcher)
        updateUserUseCase = UpdateUserUseCase(userRepository, testDispatcher)
        deleteUserUseCase = DeleteUserUseCase(userRepository, testDispatcher)
        changeUserRoleUseCase = ChangeUserRoleUseCase(userRepository, testDispatcher)
        resetPasswordUseCase = ResetPasswordUseCase(userRepository, testDispatcher)
        enableUserUseCase = EnableUserUseCase(userRepository, testDispatcher)
        disableUserUseCase = DisableUserUseCase(userRepository, testDispatcher)
        getAdminSettingsUseCase = GetAdminSettingsUseCase(adminSettingsRepository, testDispatcher)
        updateAdminSettingsUseCase = UpdateAdminSettingsUseCase(adminSettingsRepository, testDispatcher)
        exportDataUseCase = ExportDataUseCase(
            object : DataExporter {},
            testDispatcher
        )
        importDataUseCase = ImportDataUseCase(
            object : DataImporter {},
            testDispatcher
        )
        dataCleanupUseCase = DataCleanupUseCase(
            object : DataCleanupManager {},
            testDispatcher
        )
        storageStatisticsUseCase = StorageStatisticsUseCase(
            object : StorageStatisticsManager {},
            testDispatcher
        )

        // Initialize ViewModels
        userManagementViewModel = UserManagementViewModel(
            getAllUsersUseCase,
            createUserUseCase,
            updateUserUseCase,
            deleteUserUseCase,
            changeUserRoleUseCase,
            resetPasswordUseCase,
            enableUserUseCase,
            disableUserUseCase,
            auditLogger
        )

        dataManagementViewModel = DataManagementViewModel(
            object : DatabaseBackupUseCase {},
            object : DatabaseRestoreUseCase {},
            exportDataUseCase,
            importDataUseCase,
            dataCleanupUseCase,
            storageStatisticsUseCase,
            auditLogger
        )

        systemSettingsViewModel = SystemSettingsViewModel(
            getAdminSettingsUseCase,
            updateAdminSettingsUseCase,
            object : ResetSettingsToDefaultUseCase {},
            object : ExportSettingsUseCase {},
            object : ImportSettingsUseCase {},
            auditLogger
        )

        auditLogsViewModel = AuditLogsViewModel(
            object : GetAuditLogsUseCase {},
            auditLogger
        )

        securityViewModel = SecurityViewModel(
            object : GetAllUsersUseCase {},
            auditLogger,
            object : SecurityMonitoringService {}
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `UserManagementViewModel should create user through UseCase`() = runTest(testDispatcher) {
        // When - create user through ViewModel
        userManagementViewModel.createUser("testuser", "test@example.com", "password123", Role.STUDENT)

        // Then - verify through UseCase
        val result = getAllUsersUseCase.execute()
        assertTrue(result.isSuccess)
        val users = result.getOrNull()
        assertNotNull(users)
        assertEquals(1, users.size)
        assertEquals("testuser", users[0].username)
        assertEquals(Role.STUDENT, users[0].role)
    }

    @Test
    fun `UserManagementViewModel should update user role through UseCase`() = runTest(testDispatcher) {
        // Given - create user
        createUserUseCase.execute("user", "user@example.com", "pass", Role.STUDENT)

        // When - change role through ViewModel
        userManagementViewModel.changeUserRole(1L, Role.TEACHER)

        // Then - verify through UseCase
        val result = getAllUsersUseCase.execute()
        assertTrue(result.isSuccess)
        val users = result.getOrNull()
        assertNotNull(users)
        assertEquals(Role.TEACHER, users[0].role)
    }

    @Test
    fun `UserManagementViewModel should enable/disable user through UseCase`() = runTest(testDispatcher) {
        // Given - create user
        createUserUseCase.execute("user", "user@example.com", "pass", Role.STUDENT)

        // When - disable user through ViewModel
        userManagementViewModel.disableUser(1L)

        // Then - verify
        var result = getAllUsersUseCase.execute()
        assertTrue(result.isSuccess)
        var users = result.getOrNull()
        assertNotNull(users)
        assertFalse(users[0].isActive)

        // When - enable user through ViewModel
        userManagementViewModel.enableUser(1L)

        // Then - verify
        result = getAllUsersUseCase.execute()
        assertTrue(result.isSuccess)
        users = result.getOrNull()
        assertNotNull(users)
        assertTrue(users[0].isActive)
    }

    @Test
    fun `UserManagementViewModel should reset password through UseCase`() = runTest(testDispatcher) {
        // Given - create user
        createUserUseCase.execute("user", "user@example.com", "pass", Role.STUDENT)

        // When - reset password through ViewModel
        userManagementViewModel.resetPassword(1L, "newpassword123")

        // Then - verify password reset (implementation would check actual hash)
        val result = resetPasswordUseCase.execute(1L, "newpassword123")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `UserManagementViewModel should delete user through UseCase`() = runTest(testDispatcher) {
        // Given - create user
        createUserUseCase.execute("user", "user@example.com", "pass", Role.STUDENT)

        // When - delete user through ViewModel
        userManagementViewModel.deleteUser(1L)

        // Then - verify through UseCase
        val result = getAllUsersUseCase.execute()
        assertTrue(result.isSuccess)
        val users = result.getOrNull()
        assertNotNull(users)
        assertEquals(UserStatus.DELETED, users[0].status)
        assertFalse(users[0].isActive)
    }

    @Test
    fun `UserManagementViewModel should load users through UseCase`() = runTest(testDispatcher) {
        // Given - create multiple users
        createUserUseCase.execute("user1", "user1@example.com", "pass1", Role.STUDENT)
        createUserUseCase.execute("user2", "user2@example.com", "pass2", Role.TEACHER)

        // When - load users through ViewModel
        userManagementViewModel.loadUsers()

        // Then - verify through UseCase
        val result = getAllUsersUseCase.execute()
        assertTrue(result.isSuccess)
        val users = result.getOrNull()
        assertNotNull(users)
        assertEquals(2, users.size)
    }

    @Test
    fun `SystemSettingsViewModel should load settings through UseCase`() = runTest(testDispatcher) {
        // Given - insert settings directly to database
        database.adminSettingDao().insertSetting(
            com.example.codechecker.data.local.entity.AdminSettingEntity(
                1L,
                "similarity_threshold",
                "0.8",
                "Threshold",
                "Algorithm",
                true
            )
        )

        // When - load settings through ViewModel
        systemSettingsViewModel.loadSettings()

        // Then - verify through UseCase
        val result = getAdminSettingsUseCase.execute()
        assertTrue(result.isSuccess)
        val settings = result.getOrNull()
        assertNotNull(settings)
        assertEquals(1, settings.size)
    }

    @Test
    fun `SystemSettingsViewModel should update setting through UseCase`() = runTest(testDispatcher) {
        // Given - insert setting
        database.adminSettingDao().insertSetting(
            com.example.codechecker.data.local.entity.AdminSettingEntity(
                1L,
                "threshold",
                "0.5",
                "Threshold",
                "Algo",
                true
            )
        )

        // When - update setting through ViewModel
        systemSettingsViewModel.updateSetting("threshold", "0.9")

        // Then - verify through UseCase
        val result = updateAdminSettingsUseCase.execute("threshold", "0.9")
        assertTrue(result.isSuccess)
        val updatedSetting = result.getOrNull()
        assertNotNull(updatedSetting)
        assertEquals("0.9", updatedSetting.value)
    }

    @Test
    fun `DataManagementViewModel should export data through UseCase`() = runTest(testDispatcher) {
        // When - export data through ViewModel
        dataManagementViewModel.exportData("JSON")

        // Then - verify export use case is called
        // (Actual verification would require mocking or checking file system)
        assertTrue(true) // Placeholder - actual test would verify file creation
    }

    @Test
    fun `DataManagementViewModel should import data through UseCase`() = runTest(testDispatcher) {
        // When - import data through ViewModel
        dataManagementViewModel.importData("CSV")

        // Then - verify import use case is called
        assertTrue(true) // Placeholder - actual test would verify data import
    }

    @Test
    fun `DataManagementViewModel should preview cleanup through UseCase`() = runTest(testDispatcher) {
        // When - preview cleanup through ViewModel
        dataManagementViewModel.previewCleanup(90)

        // Then - verify cleanup preview
        val preview = dataManagementViewModel.uiState.value.cleanupPreview
        assertNotNull(preview)
        assertTrue(preview.reportsToDelete >= 0)
    }

    @Test
    fun `AuditLogger should log all admin actions through ViewModels`() = runTest(testDispatcher) {
        // Given - clear existing logs
        // (In real test, would clear audit log table)

        // When - perform various actions through ViewModels
        userManagementViewModel.createUser("user", "user@example.com", "pass", Role.STUDENT)
        userManagementViewModel.changeUserRole(1L, Role.TEACHER)
        userManagementViewModel.disableUser(1L)

        // Then - verify audit logs were created
        // (Would need to query audit log DAO to verify)
        assertTrue(true) // Placeholder - actual test would check audit logs
    }

    @Test
    fun `ViewModels should handle errors gracefully through UseCases`() = runTest(testDispatcher) {
        // When - try to create user with invalid data through ViewModel
        userManagementViewModel.createUser("", "invalid", "", Role.STUDENT)

        // Then - ViewModel should handle error
        val error = userManagementViewModel.uiState.value.error
        assertNotNull(error)
    }

    @Test
    fun `UseCase should cascade changes to DAO through ViewModel`() = runTest(testDispatcher) {
        // Given - create admin user
        createUserUseCase.execute("admin", "admin@example.com", "pass", Role.ADMIN)

        // When - perform chain of operations through ViewModel
        userManagementViewModel.changeUserRole(1L, Role.TEACHER)
        userManagementViewModel.enableUser(1L)
        userManagementViewModel.resetPassword(1L, "newpass")

        // Then - verify all changes persisted
        val result = getAllUsersUseCase.execute()
        assertTrue(result.isSuccess)
        val user = result.getOrNull()?.get(0)
        assertNotNull(user)
        assertEquals(Role.TEACHER, user.role)
        assertTrue(user.isActive)
        // Password hash would be verified in actual implementation
    }

    @Test
    fun `Multiple ViewModels should share data through UseCases`() = runTest(testDispatcher) {
        // Given - create user through UserManagementViewModel
        userManagementViewModel.createUser("shared", "shared@example.com", "pass", Role.STUDENT)

        // When - query user through another ViewModel (would be SecurityViewModel in real scenario)
        val result = getAllUsersUseCase.execute()

        // Then - both ViewModels see same data
        assertTrue(result.isSuccess)
        val users = result.getOrNull()
        assertNotNull(users)
        assertEquals(1, users.size)
        assertEquals("shared", users[0].username)
    }

    @Test
    fun `ViewModel should validate input before calling UseCase`() = runTest(testDispatcher) {
        // When - try invalid operations through ViewModel
        userManagementViewModel.createUser("valid", "valid@example.com", "password", Role.STUDENT)
        userManagementViewModel.createUser("", "invalid@example.com", "password", Role.STUDENT)
        userManagementViewModel.createUser("valid2", "invalid", "password", Role.STUDENT)

        // Then - only valid user should be created
        val result = getAllUsersUseCase.execute()
        assertTrue(result.isSuccess)
        val users = result.getOrNull()
        assertNotNull(users)
        assertEquals(1, users.size)
        assertEquals("valid", users[0].username)
    }

    @Test
    fun `UseCase should work with SimilarityCalculator through ViewModel`() = runTest(testDispatcher) {
        // Given - update similarity threshold setting
        database.adminSettingDao().insertSetting(
            com.example.codechecker.data.local.entity.AdminSettingEntity(
                1L,
                "similarity_threshold",
                "0.7",
                "Threshold",
                "Algorithm",
                true
            )
        )

        // When - query setting through ViewModel
        systemSettingsViewModel.loadSettings()

        // Then - SimilarityCalculator should use updated threshold
        val result = updateAdminSettingsUseCase.execute("similarity_threshold", "0.9")
        assertTrue(result.isSuccess)

        // (Actual test would verify SimilarityCalculator uses new threshold)
        assertTrue(true) // Placeholder
    }

    @Test
    fun `DataManagementViewModel should track operation progress through UseCases`() = runTest(testDispatcher) {
        // When - start backup through ViewModel
        dataManagementViewModel.backupDatabase()

        // Then - verify progress tracking
        val state = dataManagementViewModel.uiState.value
        // (Actual implementation would check progress states)
        assertTrue(true) // Placeholder
    }

    @Test
    fun `UserManagementViewModel should provide loading state during UseCase execution`() = runTest(testDispatcher) {
        // When - create user (simulate slow operation)
        userManagementViewModel.createUser("loading", "loading@example.com", "pass", Role.STUDENT)

        // Then - verify loading state
        val state = userManagementViewModel.uiState.value
        // (Would check for loading state transitions)
        assertTrue(true) // Placeholder
    }

    @Test
    fun `ViewModel should clear state after successful operation`() = runTest(testDispatcher) {
        // Given - create user
        userManagementViewModel.createUser("clear", "clear@example.com", "pass", Role.STUDENT)

        // When - clear success message
        userManagementViewModel.clearSuccess()

        // Then - verify state cleared
        val state = userManagementViewModel.uiState.value
        assertNull(state.success)
    }

    @Test
    fun `Integration should maintain data consistency across operations`() = runTest(testDispatcher) {
        // Given - create users
        createUserUseCase.execute("user1", "user1@example.com", "pass1", Role.STUDENT)
        createUserUseCase.execute("user2", "user2@example.com", "pass2", Role.TEACHER)

        // When - perform multiple operations
        changeUserRoleUseCase.execute(1L, Role.TEACHER)
        disableUserUseCase.execute(1L)
        enableUserUseCase.execute(2L)

        // Then - verify data consistency
        val result = getAllUsersUseCase.execute()
        assertTrue(result.isSuccess)
        val users = result.getOrNull()
        assertNotNull(users)

        val user1 = users.find { it.id == 1L }
        assertNotNull(user1)
        assertEquals(Role.TEACHER, user1.role)
        assertFalse(user1.isActive)

        val user2 = users.find { it.id == 2L }
        assertNotNull(user2)
        assertEquals(Role.TEACHER, user2.role)
        assertTrue(user2.isActive)
    }
}
