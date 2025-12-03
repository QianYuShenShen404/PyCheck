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
 * Unit tests for DeleteUserUseCase
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DeleteUserUseCaseTest {

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var deleteUserUseCase: DeleteUserUseCase

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        deleteUserUseCase = DeleteUserUseCase(userRepository, testDispatcher)
    }

    @Test
    fun `execute should soft delete user successfully`() = runTest(testDispatcher) {
        // Given
        val userId = 2L

        val deletedUser = User(
            id = userId,
            username = "testuser",
            email = "test@example.com",
            role = Role.STUDENT,
            isActive = false,
            status = UserStatus.DELETED
        )

        whenever(userRepository.deleteUser(userId)).thenReturn(deletedUser)

        // When
        val result = deleteUserUseCase.execute(userId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(deletedUser, result.getOrNull())
        assertFalse(result.getOrNull()?.isActive ?: true, "User should be marked as inactive")
        assertEquals(UserStatus.DELETED, result.getOrNull()?.status)
        verify(userRepository).deleteUser(userId)
    }

    @Test
    fun `execute should delete admin user`() = runTest(testDispatcher) {
        // Given
        val userId = 1L

        val deletedUser = User(
            id = userId,
            username = "admin",
            email = "admin@example.com",
            role = Role.ADMIN,
            isActive = false,
            status = UserStatus.DELETED
        )

        whenever(userRepository.deleteUser(userId)).thenReturn(deletedUser)

        // When
        val result = deleteUserUseCase.execute(userId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(Role.ADMIN, result.getOrNull()?.role)
        verify(userRepository).deleteUser(userId)
    }

    @Test
    fun `execute should delete teacher user`() = runTest(testDispatcher) {
        // Given
        val userId = 3L

        val deletedUser = User(
            id = userId,
            username = "teacher1",
            email = "teacher1@example.com",
            role = Role.TEACHER,
            isActive = false,
            status = UserStatus.DELETED
        )

        whenever(userRepository.deleteUser(userId)).thenReturn(deletedUser)

        // When
        val result = deleteUserUseCase.execute(userId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(Role.TEACHER, result.getOrNull()?.role)
        verify(userRepository).deleteUser(userId)
    }

    @Test
    fun `execute should return failure when user not found`() = runTest(testDispatcher) {
        // Given
        val userId = 999L
        val exception = IllegalStateException("User not found")

        whenever(userRepository.deleteUser(userId)).thenThrow(exception)

        // When
        val result = deleteUserUseCase.execute(userId)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(userRepository).deleteUser(userId)
    }

    @Test
    fun `execute should return failure when repository throws exception`() = runTest(testDispatcher) {
        // Given
        val userId = 2L
        val exception = RuntimeException("Database error")

        whenever(userRepository.deleteUser(userId)).thenThrow(exception)

        // When
        val result = deleteUserUseCase.execute(userId)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(userRepository).deleteUser(userId)
    }

    @Test
    fun `execute should handle invalid userId`() = runTest(testDispatcher) {
        // Given
        val userId = 0L

        // When
        val result = deleteUserUseCase.execute(userId)

        // Then
        verify(userRepository, never()).deleteUser(any())
        assertTrue(result.isFailure)
    }

    @Test
    fun `execute should perform soft delete not hard delete`() = runTest(testDispatcher) {
        // Given
        val userId = 1L

        val deletedUser = User(
            id = userId,
            username = "user",
            email = "user@example.com",
            role = Role.STUDENT,
            isActive = false,
            status = UserStatus.DELETED
        )

        whenever(userRepository.deleteUser(userId)).thenReturn(deletedUser)

        // When
        val result = deleteUserUseCase.execute(userId)

        // Then
        assertTrue(result.isSuccess)
        // Verify it's a soft delete - user record should still exist but be marked as deleted
        assertNotNull(result.getOrNull())
        verify(userRepository).deleteUser(userId)
        // Verify repository method was called (soft delete), not a hard delete method
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `execute should return success for different user types`() = runTest(testDispatcher) {
        // Given
        val userId = 1L

        val deletedUser = User(
            id = userId,
            username = "user",
            email = "user@example.com",
            role = Role.STUDENT,
            isActive = false,
            status = UserStatus.DELETED
        )

        whenever(userRepository.deleteUser(any())).thenReturn(deletedUser)

        // Test deleting different user types
        listOf(Role.STUDENT, Role.TEACHER, Role.ADMIN).forEach { role ->
            val user = deletedUser.copy(role = role)
            whenever(userRepository.deleteUser(any())).thenReturn(user)

            val result = deleteUserUseCase.execute(userId)
            assertTrue(result.isSuccess, "Should delete user with role: $role")
        }
    }
}
