package com.example.codechecker.domain.usecase

import com.example.codechecker.domain.model.Role
import com.example.codechecker.domain.model.User
import com.example.codechecker.domain.model.UserStatus
import com.example.codechecker.domain.repository.UserRepository
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
 * Unit tests for EnableUserUseCase
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EnableUserUseCaseTest {

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var enableUserUseCase: EnableUserUseCase

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        enableUserUseCase = EnableUserUseCase(userRepository, testDispatcher)
    }

    @Test
    fun `execute should enable user successfully`() = runTest(testDispatcher) {
        // Given
        val userId = 2L

        val enabledUser = User(
            id = userId,
            username = "testuser",
            email = "test@example.com",
            role = Role.STUDENT,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        whenever(userRepository.enableUser(userId)).thenReturn(enabledUser)

        // When
        val result = enableUserUseCase.execute(userId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(enabledUser, result.getOrNull())
        assertTrue(result.getOrNull()?.isActive ?: false, "User should be active")
        assertEquals(UserStatus.ACTIVE, result.getOrNull()?.status)
        verify(userRepository).enableUser(userId)
    }

    @Test
    fun `execute should enable disabled admin user`() = runTest(testDispatcher) {
        // Given
        val userId = 1L

        val enabledAdmin = User(
            id = userId,
            username = "admin",
            email = "admin@example.com",
            role = Role.ADMIN,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        whenever(userRepository.enableUser(userId)).thenReturn(enabledAdmin)

        // When
        val result = enableUserUseCase.execute(userId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(Role.ADMIN, result.getOrNull()?.role)
        verify(userRepository).enableUser(userId)
    }

    @Test
    fun `execute should enable teacher user`() = runTest(testDispatcher) {
        // Given
        val userId = 3L

        val enabledTeacher = User(
            id = userId,
            username = "teacher1",
            email = "teacher1@example.com",
            role = Role.TEACHER,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        whenever(userRepository.enableUser(userId)).thenReturn(enabledTeacher)

        // When
        val result = enableUserUseCase.execute(userId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(Role.TEACHER, result.getOrNull()?.role)
        verify(userRepository).enableUser(userId)
    }

    @Test
    fun `execute should return failure when user not found`() = runTest(testDispatcher) {
        // Given
        val userId = 999L
        val exception = IllegalStateException("User not found")

        whenever(userRepository.enableUser(userId)).thenThrow(exception)

        // When
        val result = enableUserUseCase.execute(userId)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(userRepository).enableUser(userId)
    }

    @Test
    fun `execute should return failure when repository throws exception`() = runTest(testDispatcher) {
        // Given
        val userId = 2L
        val exception = RuntimeException("Database error")

        whenever(userRepository.enableUser(userId)).thenThrow(exception)

        // When
        val result = enableUserUseCase.execute(userId)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(userRepository).enableUser(userId)
    }

    @Test
    fun `execute should handle invalid userId`() = runTest(testDispatcher) {
        // Given
        val userId = 0L

        // When
        val result = enableUserUseCase.execute(userId)

        // Then
        verify(userRepository, never()).enableUser(any())
        assertTrue(result.isFailure)
    }

    @Test
    fun `execute should enable user with any role`() = runTest(testDispatcher) {
        // Given
        val userId = 1L

        val user = User(
            id = userId,
            username = "user",
            email = "user@example.com",
            role = Role.STUDENT,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        whenever(userRepository.enableUser(any())).thenReturn(user)

        // Test enabling users with different roles
        listOf(Role.STUDENT, Role.TEACHER, Role.ADMIN).forEach { role ->
            val userWithRole = user.copy(role = role)
            whenever(userRepository.enableUser(any())).thenReturn(userWithRole)

            val result = enableUserUseCase.execute(userId)
            assertTrue(result.isSuccess, "Should enable user with role: $role")
        }
    }
}
