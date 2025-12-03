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
 * Unit tests for CreateUserUseCase
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CreateUserUseCaseTest {

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var createUserUseCase: CreateUserUseCase

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        createUserUseCase = CreateUserUseCase(userRepository, testDispatcher)
    }

    @Test
    fun `execute should create user successfully`() = runTest(testDispatcher) {
        // Given
        val username = "newuser"
        val email = "newuser@example.com"
        val password = "password123"
        val role = Role.STUDENT

        val createdUser = User(
            id = 3L,
            username = username,
            email = email,
            role = role,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        whenever(userRepository.createUser(username, email, password, role)).thenReturn(createdUser)

        // When
        val result = createUserUseCase.execute(username, email, password, role)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(createdUser, result.getOrNull())
        verify(userRepository).createUser(username, email, password, role)
    }

    @Test
    fun `execute should create admin user successfully`() = runTest(testDispatcher) {
        // Given
        val username = "newadmin"
        val email = "admin@test.com"
        val password = "adminpass"
        val role = Role.ADMIN

        val createdUser = User(
            id = 1L,
            username = username,
            email = email,
            role = role,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        whenever(userRepository.createUser(username, email, password, role)).thenReturn(createdUser)

        // When
        val result = createUserUseCase.execute(username, email, password, role)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(createdUser, result.getOrNull())
        assertEquals(Role.ADMIN, result.getOrNull()?.role)
    }

    @Test
    fun `execute should return failure when repository throws exception`() = runTest(testDispatcher) {
        // Given
        val exception = RuntimeException("User already exists")
        whenever(
            userRepository.createUser(
                any(),
                any(),
                any(),
                any()
            )
        ).thenThrow(exception)

        // When
        val result = createUserUseCase.execute("existinguser", "test@example.com", "pass", Role.STUDENT)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `execute should handle empty username`() = runTest(testDispatcher) {
        // Given
        val username = ""
        val email = "test@example.com"
        val password = "password"
        val role = Role.STUDENT

        // When
        val result = createUserUseCase.execute(username, email, password, role)

        // Then - repository should not be called
        verify(userRepository, never()).createUser(any(), any(), any(), any())
        assertTrue(result.isFailure)
    }

    @Test
    fun `execute should handle empty email`() = runTest(testDispatcher) {
        // Given
        val username = "testuser"
        val email = ""
        val password = "password"
        val role = Role.STUDENT

        // When
        val result = createUserUseCase.execute(username, email, password, role)

        // Then
        verify(userRepository, never()).createUser(any(), any(), any(), any())
        assertTrue(result.isFailure)
    }

    @Test
    fun `execute should handle all valid roles`() = runTest(testDispatcher) {
        // Given
        val username = "user"
        val email = "user@example.com"
        val password = "password"

        val createdUser = User(
            id = 1L,
            username = username,
            email = email,
            role = Role.STUDENT,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        whenever(userRepository.createUser(any(), any(), any(), any())).thenReturn(createdUser)

        // Test all roles
        val roles = listOf(Role.STUDENT, Role.TEACHER, Role.ADMIN)
        roles.forEach { role ->
            val result = createUserUseCase.execute(username, email, password, role)
            assertTrue(result.isSuccess, "Should succeed for role: $role")
        }
    }
}
