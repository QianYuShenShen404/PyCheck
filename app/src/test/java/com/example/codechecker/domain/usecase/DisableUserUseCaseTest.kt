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
 * Unit tests for DisableUserUseCase
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DisableUserUseCaseTest {

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var disableUserUseCase: DisableUserUseCase

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        disableUserUseCase = DisableUserUseCase(userRepository, testDispatcher)
    }

    @Test
    fun `execute should disable user successfully`() = runTest(testDispatcher) {
        // Given
        val userId = 2L

        val disabledUser = User(
            id = userId,
            username = "testuser",
            email = "test@example.com",
            role = Role.STUDENT,
            isActive = false,
            status = UserStatus.DISABLED
        )

        whenever(userRepository.disableUser(userId)).thenReturn(disabledUser)

        // When
        val result = disableUserUseCase.execute(userId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(disabledUser, result.getOrNull())
        assertFalse(result.getOrNull()?.isActive ?: true, "User should be inactive")
        assertEquals(UserStatus.DISABLED, result.getOrNull()?.status)
        verify(userRepository).disableUser(userId)
    }

    @Test
    fun `execute should disable admin user`() = runTest(testDispatcher) {
        // Given
        val userId = 1L

        val disabledAdmin = User(
            id = userId,
            username = "admin",
            email = "admin@example.com",
            role = Role.ADMIN,
            isActive = false,
            status = UserStatus.DISABLED
        )

        whenever(userRepository.disableUser(userId)).thenReturn(disabledAdmin)

        // When
        val result = disableUserUseCase.execute(userId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(Role.ADMIN, result.getOrNull()?.role)
        verify(userRepository).disableUser(userId)
    }

    @Test
    fun `execute should disable teacher user`() = runTest(testDispatcher) {
        // Given
        val userId = 3L

        val disabledTeacher = User(
            id = userId,
            username = "teacher1",
            email = "teacher1@example.com",
            role = Role.TEACHER,
            isActive = false,
            status = UserStatus.DISABLED
        )

        whenever(userRepository.disableUser(userId)).thenReturn(disabledTeacher)

        // When
        val result = disableUserUseCase.execute(userId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(Role.TEACHER, result.getOrNull()?.role)
        verify(userRepository).disableUser(userId)
    }

    @Test
    fun `execute should return failure when user not found`() = runTest(testDispatcher) {
        // Given
        val userId = 999L
        val exception = IllegalStateException("User not found")

        whenever(userRepository.disableUser(userId)).thenThrow(exception)

        // When
        val result = disableUserUseCase.execute(userId)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(userRepository).disableUser(userId)
    }

    @Test
    fun `execute should return failure when repository throws exception`() = runTest(testDispatcher) {
        // Given
        val userId = 2L
        val exception = RuntimeException("Database error")

        whenever(userRepository.disableUser(userId)).thenThrow(exception)

        // When
        val result = disableUserUseCase.execute(userId)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(userRepository).disableUser(userId)
    }

    @Test
    fun `execute should handle invalid userId`() = runTest(testDispatcher) {
        // Given
        val userId = 0L

        // When
        val result = disableUserUseCase.execute(userId)

        // Then
        verify(userRepository, never()).disableUser(any())
        assertTrue(result.isFailure)
    }

    @Test
    fun `execute should disable user with any role`() = runTest(testDispatcher) {
        // Given
        val userId = 1L

        val user = User(
            id = userId,
            username = "user",
            email = "user@example.com",
            role = Role.STUDENT,
            isActive = false,
            status = UserStatus.DISABLED
        )

        whenever(userRepository.disableUser(any())).thenReturn(user)

        // Test disabling users with different roles
        listOf(Role.STUDENT, Role.TEACHER, Role.ADMIN).forEach { role ->
            val userWithRole = user.copy(role = role)
            whenever(userRepository.disableUser(any())).thenReturn(userWithRole)

            val result = disableUserUseCase.execute(userId)
            assertTrue(result.isSuccess, "Should disable user with role: $role")
        }
    }

    @Test
    fun `execute should not delete user only disable`() = runTest(testDispatcher) {
        // Given
        val userId = 1L

        val disabledUser = User(
            id = userId,
            username = "user",
            email = "user@example.com",
            role = Role.STUDENT,
            isActive = false,
            status = UserStatus.DISABLED
        )

        whenever(userRepository.disableUser(userId)).thenReturn(disabledUser)

        // When
        val result = disableUserUseCase.execute(userId)

        // Then
        assertTrue(result.isSuccess)
        // Verify it's disable not delete - user should exist but be disabled
        assertNotNull(result.getOrNull())
        assertEquals(UserStatus.DISABLED, result.getOrNull()?.status)
        verify(userRepository).disableUser(userId)
        // Verify no delete method was called
        verifyNoMoreInteractions(userRepository)
    }
}
