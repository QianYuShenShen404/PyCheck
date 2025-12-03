package com.example.codechecker.domain.usecase

import com.example.codechecker.domain.model.Role
import com.example.codechecker.domain.model.User
import com.example.codechecker.domain.model.UserStatus
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
 * Unit tests for permission checks and role-based access control
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PermissionCheckTest {

    @Mock
    private lateinit var userRepository: com.example.codechecker.domain.repository.UserRepository

    private lateinit var checkUserPermissionUseCase: CheckUserPermissionUseCase

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        checkUserPermissionUseCase = CheckUserPermissionUseCase(userRepository, testDispatcher)
    }

    @Test
    fun `admin can access all features`() = runTest(testDispatcher) {
        // Given
        val adminUser = User(
            id = 1L,
            username = "admin",
            email = "admin@example.com",
            role = Role.ADMIN,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        // Admin permissions
        val adminPermissions = listOf(
            "user:create",
            "user:read",
            "user:update",
            "user:delete",
            "user:enable",
            "user:disable",
            "user:role_change",
            "user:password_reset",
            "settings:read",
            "settings:update",
            "data:export",
            "data:import",
            "data:backup",
            "data:restore",
            "data:cleanup",
            "audit:read",
            "security:read",
            "security:force_logout"
        )

        // Test all admin permissions
        adminPermissions.forEach { permission ->
            val result = checkUserPermissionUseCase.execute(adminUser, permission)
            assertTrue(result, "Admin should have permission: $permission")
        }
    }

    @Test
    fun `teacher can access limited features`() = runTest(testDispatcher) {
        // Given
        val teacherUser = User(
            id = 2L,
            username = "teacher1",
            email = "teacher1@example.com",
            role = Role.TEACHER,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        // Teacher permissions
        val teacherPermissions = listOf(
            "user:read",
            "data:export",
            "audit:read"
        )

        // Test teacher permissions
        teacherPermissions.forEach { permission ->
            val result = checkUserPermissionUseCase.execute(teacherUser, permission)
            assertTrue(result, "Teacher should have permission: $permission")
        }

        // Teacher should not have admin-only permissions
        val adminOnlyPermissions = listOf(
            "user:create",
            "user:update",
            "user:delete",
            "user:enable",
            "user:disable",
            "user:role_change",
            "user:password_reset",
            "settings:update",
            "data:import",
            "data:backup",
            "data:restore",
            "data:cleanup",
            "security:force_logout"
        )

        adminOnlyPermissions.forEach { permission ->
            val result = checkUserPermissionUseCase.execute(teacherUser, permission)
            assertFalse(result, "Teacher should NOT have permission: $permission")
        }
    }

    @Test
    fun `student has minimal permissions`() = runTest(testDispatcher) {
        // Given
        val studentUser = User(
            id = 3L,
            username = "student1",
            email = "student1@example.com",
            role = Role.STUDENT,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        // Student permissions (very limited)
        val studentPermissions = listOf(
            "user:read" // Only can read their own profile
        )

        // Test student permissions
        studentPermissions.forEach { permission ->
            val result = checkUserPermissionUseCase.execute(studentUser, permission)
            assertTrue(result, "Student should have permission: $permission")
        }

        // Student should not have any admin or teacher permissions
        val restrictedPermissions = listOf(
            "user:create",
            "user:update",
            "user:delete",
            "user:enable",
            "user:disable",
            "user:role_change",
            "user:password_reset",
            "settings:read",
            "settings:update",
            "data:export",
            "data:import",
            "data:backup",
            "data:restore",
            "data:cleanup",
            "audit:read",
            "security:read",
            "security:force_logout"
        )

        restrictedPermissions.forEach { permission ->
            val result = checkUserPermissionUseCase.execute(studentUser, permission)
            assertFalse(result, "Student should NOT have permission: $permission")
        }
    }

    @Test
    fun `disabled user cannot access any features`() = runTest(testDispatcher) {
        // Given
        val disabledUser = User(
            id = 4L,
            username = "disableduser",
            email = "disabled@example.com",
            role = Role.STUDENT,
            isActive = false,
            status = UserStatus.DISABLED
        )

        // Even basic permissions should be denied for disabled users
        val permissions = listOf(
            "user:read",
            "data:export"
        )

        permissions.forEach { permission ->
            val result = checkUserPermissionUseCase.execute(disabledUser, permission)
            assertFalse(result, "Disabled user should NOT have permission: $permission")
        }
    }

    @Test
    fun `deleted user cannot access any features`() = runTest(testDispatcher) {
        // Given
        val deletedUser = User(
            id = 5L,
            username = "deleteduser",
            email = "deleted@example.com",
            role = Role.TEACHER,
            isActive = false,
            status = UserStatus.DELETED
        )

        val permissions = listOf(
            "user:read",
            "data:export",
            "audit:read"
        )

        permissions.forEach { permission ->
            val result = checkUserPermissionUseCase.execute(deletedUser, permission)
            assertFalse(result, "Deleted user should NOT have permission: $permission")
        }
    }

    @Test
    fun `admin can manage other admin users`() = runTest(testDispatcher) {
        // Given
        val adminUser = User(
            id = 1L,
            username = "admin1",
            email = "admin1@example.com",
            role = Role.ADMIN,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        val targetAdmin = User(
            id = 2L,
            username = "admin2",
            email = "admin2@example.com",
            role = Role.ADMIN,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        // Admin should be able to manage other admins
        val managementPermissions = listOf(
            "user:update",
            "user:disable",
            "user:enable"
        )

        managementPermissions.forEach { permission ->
            val result = checkUserPermissionUseCase.execute(adminUser, permission, targetAdmin.id)
            assertTrue(result, "Admin should be able to $permission other admins")
        }
    }

    @Test
    fun `inactive admin cannot access features`() = runTest(testDispatcher) {
        // Given
        val inactiveAdmin = User(
            id = 1L,
            username = "inactiveadmin",
            email = "inactive@example.com",
            role = Role.ADMIN,
            isActive = false,
            status = UserStatus.DISABLED
        )

        val permissions = listOf(
            "user:create",
            "settings:update",
            "data:backup"
        )

        permissions.forEach { permission ->
            val result = checkUserPermissionUseCase.execute(inactiveAdmin, permission)
            assertFalse(result, "Inactive admin should NOT have permission: $permission")
        }
    }

    @Test
    fun `user can read their own profile`() = runTest(testDispatcher) {
        // Given
        val userId = 3L
        val user = User(
            id = userId,
            username = "student1",
            email = "student1@example.com",
            role = Role.STUDENT,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        // Users can read their own profile
        val result = checkUserPermissionUseCase.execute(user, "user:read", userId)
        assertTrue(result, "User should be able to read their own profile")
    }

    @Test
    fun `student cannot read other users profiles`() = runTest(testDispatcher) {
        // Given
        val student = User(
            id = 3L,
            username = "student1",
            email = "student1@example.com",
            role = Role.STUDENT,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        val otherUserId = 4L

        // Students cannot read other users' profiles
        val result = checkUserPermissionUseCase.execute(student, "user:read", otherUserId)
        assertFalse(result, "Student should NOT be able to read other users' profiles")
    }

    @Test
    fun `teacher can read student profiles`() = runTest(testDispatcher) {
        // Given
        val teacher = User(
            id = 2L,
            username = "teacher1",
            email = "teacher1@example.com",
            role = Role.TEACHER,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        val studentId = 3L

        // Teachers can read student profiles
        val result = checkUserPermissionUseCase.execute(teacher, "user:read", studentId)
        assertTrue(result, "Teacher should be able to read student profiles")
    }

    @Test
    fun `permission check handles null permission`() = runTest(testDispatcher) {
        // Given
        val admin = User(
            id = 1L,
            username = "admin",
            email = "admin@example.com",
            role = Role.ADMIN,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        // When
        val result = checkUserPermissionUseCase.execute(admin, null)

        // Then
        assertFalse(result, "Null permission should be denied")
    }

    @Test
    fun `permission check handles empty permission`() = runTest(testDispatcher) {
        // Given
        val admin = User(
            id = 1L,
            username = "admin",
            email = "admin@example.com",
            role = Role.ADMIN,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        // When
        val result = checkUserPermissionUseCase.execute(admin, "")

        // Then
        assertFalse(result, "Empty permission should be denied")
    }

    @Test
    fun `role hierarchy is respected`() = runTest(testDispatcher) {
        // Verify that ADMIN > TEACHER > STUDENT in terms of permissions
        val admin = User(1L, "admin", "a@a.com", Role.ADMIN, true, UserStatus.ACTIVE)
        val teacher = User(2L, "teacher", "t@t.com", Role.TEACHER, true, UserStatus.ACTIVE)
        val student = User(3L, "student", "s@s.com", Role.STUDENT, true, UserStatus.ACTIVE)

        // Admin has all permissions
        assertTrue(checkUserPermissionUseCase.execute(admin, "user:delete"))

        // Teacher has some permissions but not all
        assertTrue(checkUserPermissionUseCase.execute(teacher, "user:read"))
        assertFalse(checkUserPermissionUseCase.execute(teacher, "user:delete"))

        // Student has minimal permissions
        assertTrue(checkUserPermissionUseCase.execute(student, "user:read"))
        assertFalse(checkUserPermissionUseCase.execute(student, "user:update"))
    }
}
