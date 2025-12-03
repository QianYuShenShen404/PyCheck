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
 * Unit tests for ResetPasswordUseCase
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ResetPasswordUseCaseTest {

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var resetPasswordUseCase: ResetPasswordUseCase

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        resetPasswordUseCase = ResetPasswordUseCase(userRepository, testDispatcher)
    }

    @Test
    fun `execute should reset password successfully`() = runTest(testDispatcher) {
        // Given
        val userId = 2L
        val newPassword = "newPassword123"

        val user = User(
            id = userId,
            username = "testuser",
            email = "test@example.com",
            role = Role.STUDENT,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        whenever(userRepository.resetPassword(userId, newPassword)).thenReturn(user)

        // When
        val result = resetPasswordUseCase.execute(userId, newPassword)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(user, result.getOrNull())
        verify(userRepository).resetPassword(userId, newPassword)
    }

    @Test
    fun `execute should reset password for admin user`() = runTest(testDispatcher) {
        // Given
        val userId = 1L
        val newPassword = "AdminNewPass123"

        val adminUser = User(
            id = userId,
            username = "admin",
            email = "admin@example.com",
            role = Role.ADMIN,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        whenever(userRepository.resetPassword(userId, newPassword)).thenReturn(adminUser)

        // When
        val result = resetPasswordUseCase.execute(userId, newPassword)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(Role.ADMIN, result.getOrNull()?.role)
        verify(userRepository).resetPassword(userId, newPassword)
    }

    @Test
    fun `execute should reset password for teacher user`() = runTest(testDispatcher) {
        // Given
        val userId = 3L
        val newPassword = "TeacherPass456"

        val teacherUser = User(
            id = userId,
            username = "teacher1",
            email = "teacher1@example.com",
            role = Role.TEACHER,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        whenever(userRepository.resetPassword(userId, newPassword)).thenReturn(teacherUser)

        // When
        val result = resetPasswordUseCase.execute(userId, newPassword)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(Role.TEACHER, result.getOrNull()?.role)
        verify(userRepository).resetPassword(userId, newPassword)
    }

    @Test
    fun `execute should return failure when user not found`() = runTest(testDispatcher) {
        // Given
        val userId = 999L
        val newPassword = "newpassword"
        val exception = IllegalStateException("User not found")

        whenever(userRepository.resetPassword(userId, newPassword)).thenThrow(exception)

        // When
        val result = resetPasswordUseCase.execute(userId, newPassword)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(userRepository).resetPassword(userId, newPassword)
    }

    @Test
    fun `execute should return failure when repository throws exception`() = runTest(testDispatcher) {
        // Given
        val userId = 2L
        val newPassword = "newpassword"
        val exception = RuntimeException("Database error")

        whenever(userRepository.resetPassword(userId, newPassword)).thenThrow(exception)

        // When
        val result = resetPasswordUseCase.execute(userId, newPassword)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(userRepository).resetPassword(userId, newPassword)
    }

    @Test
    fun `execute should handle invalid userId`() = runTest(testDispatcher) {
        // Given
        val userId = 0L
        val newPassword = "password"

        // When
        val result = resetPasswordUseCase.execute(userId, newPassword)

        // Then
        verify(userRepository, never()).resetPassword(any(), any())
        assertTrue(result.isFailure)
    }

    @Test
    fun `execute should handle empty password`() = runTest(testDispatcher) {
        // Given
        val userId = 1L
        val newPassword = ""

        // When
        val result = resetPasswordUseCase.execute(userId, newPassword)

        // Then
        verify(userRepository, never()).resetPassword(any(), any())
        assertTrue(result.isFailure)
    }

    @Test
    fun `execute should handle short password`() = runTest(testDispatcher) {
        // Given
        val userId = 1L
        val newPassword = "123"

        // When
        val result = resetPasswordUseCase.execute(userId, newPassword)

        // Then
        verify(userRepository, never()).resetPassword(any(), any())
        assertTrue(result.isFailure)
    }

    @Test
    fun `execute should accept strong password`() = runTest(testDispatcher) {
        // Given
        val userId = 1L
        val newPassword = "StrongPass123!@#"

        val user = User(
            id = userId,
            username = "user",
            email = "user@example.com",
            role = Role.STUDENT,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        whenever(userRepository.resetPassword(userId, newPassword)).thenReturn(user)

        // When
        val result = resetPasswordUseCase.execute(userId, newPassword)

        // Then
        assertTrue(result.isSuccess)
        verify(userRepository).resetPassword(userId, newPassword)
    }

    @Test
    fun `execute should reset password for disabled user`() = runTest(testDispatcher) {
        // Given
        val userId = 2L
        val newPassword = "newpassword"

        val disabledUser = User(
            id = userId,
            username = "disableduser",
            email = "disabled@example.com",
            role = Role.STUDENT,
            isActive = false,
            status = UserStatus.DISABLED
        )

        whenever(userRepository.resetPassword(userId, newPassword)).thenReturn(disabledUser)

        // When
        val result = resetPasswordUseCase.execute(userId, newPassword)

        // Then
        // Admin can reset password even for disabled users
        assertTrue(result.isSuccess)
        verify(userRepository).resetPassword(userId, newPassword)
    }

    @Test
    fun `execute should accept various password formats`() = runTest(testDispatcher) {
        // Given
        val userId = 1L
        val passwords = listOf(
            "Password123",
            "MyP@ssw0rd",
            "CamelCase123!",
            "lowercase123",
            "UPPERCASE123",
            "Mix3d_Cas3!@#"
        )

        val user = User(
            id = userId,
            username = "user",
            email = "user@example.com",
            role = Role.STUDENT,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        whenever(userRepository.resetPassword(any(), any())).thenReturn(user)

        // Test various password formats
        passwords.forEach { password ->
            val result = resetPasswordUseCase.execute(userId, password)
            assertTrue(result.isSuccess, "Should accept password: $password")
        }
    }
}
