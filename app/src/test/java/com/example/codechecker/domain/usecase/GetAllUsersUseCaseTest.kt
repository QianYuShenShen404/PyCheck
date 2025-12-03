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
 * Unit tests for GetAllUsersUseCase
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetAllUsersUseCaseTest {

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var getAllUsersUseCase: GetAllUsersUseCase

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getAllUsersUseCase = GetAllUsersUseCase(userRepository, testDispatcher)
    }

    @Test
    fun `execute should return all users successfully`() = runTest(testDispatcher) {
        // Given
        val mockUsers = listOf(
            User(
                id = 1L,
                username = "admin",
                email = "admin@example.com",
                role = Role.ADMIN,
                isActive = true,
                status = UserStatus.ACTIVE
            ),
            User(
                id = 2L,
                username = "teacher1",
                email = "teacher1@example.com",
                role = Role.TEACHER,
                isActive = true,
                status = UserStatus.ACTIVE
            )
        )
        whenever(userRepository.getAllUsers()).thenReturn(mockUsers)

        // When
        val result = getAllUsersUseCase.execute()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(mockUsers, result.getOrNull())
        verify(userRepository).getAllUsers()
    }

    @Test
    fun `execute should return failure when repository throws exception`() = runTest(testDispatcher) {
        // Given
        val exception = RuntimeException("Database error")
        whenever(userRepository.getAllUsers()).thenThrow(exception)

        // When
        val result = getAllUsersUseCase.execute()

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(userRepository).getAllUsers()
    }

    @Test
    fun `execute should return empty list when no users exist`() = runTest(testDispatcher) {
        // Given
        val emptyList = emptyList<User>()
        whenever(userRepository.getAllUsers()).thenReturn(emptyList)

        // When
        val result = getAllUsersUseCase.execute()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(emptyList, result.getOrNull())
        assertEquals(0, result.getOrNull()?.size)
        verify(userRepository).getAllUsers()
    }
}
