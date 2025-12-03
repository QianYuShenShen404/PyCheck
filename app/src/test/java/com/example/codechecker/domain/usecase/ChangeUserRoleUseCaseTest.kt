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
 * Unit tests for ChangeUserRoleUseCase
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ChangeUserRoleUseCaseTest {

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var changeUserRoleUseCase: ChangeUserRoleUseCase

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        changeUserRoleUseCase = ChangeUserRoleUseCase(userRepository, testDispatcher)
    }

    @Test
    fun `execute should change user role successfully`() = runTest(testDispatcher) {
        // Given
        val userId = 2L
        val newRole = Role.TEACHER

        val updatedUser = User(
            id = userId,
            username = "testuser",
            email = "test@example.com",
            role = newRole,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        whenever(userRepository.updateUserRole(userId, newRole)).thenReturn(updatedUser)

        // When
        val result = changeUserRoleUseCase.execute(userId, newRole)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(updatedUser, result.getOrNull())
        verify(userRepository).updateUserRole(userId, newRole)
    }

    @Test
    fun `execute should change student to teacher`() = runTest(testDispatcher) {
        // Given
        val userId = 1L
        val newRole = Role.TEACHER

        val updatedUser = User(
            id = userId,
            username = "student1",
            email = "student1@example.com",
            role = newRole,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        whenever(userRepository.updateUserRole(userId, newRole)).thenReturn(updatedUser)

        // When
        val result = changeUserRoleUseCase.execute(userId, newRole)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(Role.TEACHER, result.getOrNull()?.role)
        verify(userRepository).updateUserRole(userId, newRole)
    }

    @Test
    fun `execute should change teacher to admin`() = runTest(testDispatcher) {
        // Given
        val userId = 3L
        val newRole = Role.ADMIN

        val updatedUser = User(
            id = userId,
            username = "teacher1",
            email = "teacher1@example.com",
            role = newRole,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        whenever(userRepository.updateUserRole(userId, newRole)).thenReturn(updatedUser)

        // When
        val result = changeUserRoleUseCase.execute(userId, newRole)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(Role.ADMIN, result.getOrNull()?.role)
        verify(userRepository).updateUserRole(userId, newRole)
    }

    @Test
    fun `execute should change admin to student`() = runTest(testDispatcher) {
        // Given
        val userId = 1L
        val newRole = Role.STUDENT

        val updatedUser = User(
            id = userId,
            username = "admin",
            email = "admin@example.com",
            role = newRole,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        whenever(userRepository.updateUserRole(userId, newRole)).thenReturn(updatedUser)

        // When
        val result = changeUserRoleUseCase.execute(userId, newRole)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(Role.STUDENT, result.getOrNull()?.role)
        verify(userRepository).updateUserRole(userId, newRole)
    }

    @Test
    fun `execute should return failure when user not found`() = runTest(testDispatcher) {
        // Given
        val userId = 999L
        val newRole = Role.TEACHER
        val exception = IllegalStateException("User not found")

        whenever(userRepository.updateUserRole(userId, newRole)).thenThrow(exception)

        // When
        val result = changeUserRoleUseCase.execute(userId, newRole)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(userRepository).updateUserRole(userId, newRole)
    }

    @Test
    fun `execute should return failure when repository throws exception`() = runTest(testDispatcher) {
        // Given
        val userId = 2L
        val newRole = Role.TEACHER
        val exception = RuntimeException("Database error")

        whenever(userRepository.updateUserRole(userId, newRole)).thenThrow(exception)

        // When
        val result = changeUserRoleUseCase.execute(userId, newRole)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(userRepository).updateUserRole(userId, newRole)
    }

    @Test
    fun `execute should handle invalid userId`() = runTest(testDispatcher) {
        // Given
        val userId = 0L
        val newRole = Role.STUDENT

        // When
        val result = changeUserRoleUseCase.execute(userId, newRole)

        // Then
        verify(userRepository, never()).updateUserRole(any(), any())
        assertTrue(result.isFailure)
    }

    @Test
    fun `execute should accept all valid role transitions`() = runTest(testDispatcher) {
        // Given
        val userId = 1L
        val roles = listOf(Role.STUDENT, Role.TEACHER, Role.ADMIN)

        val updatedUser = User(
            id = userId,
            username = "user",
            email = "user@example.com",
            role = Role.STUDENT,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        whenever(userRepository.updateUserRole(any(), any())).thenReturn(updatedUser)

        // Test all role transitions
        roles.forEach { newRole ->
            val result = changeUserRoleUseCase.execute(userId, newRole)
            assertTrue(result.isSuccess, "Should accept transition to $newRole")
        }
    }
}
