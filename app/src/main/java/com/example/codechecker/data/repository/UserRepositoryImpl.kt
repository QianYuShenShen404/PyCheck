package com.example.codechecker.data.repository

import com.example.codechecker.data.local.dao.UserDao
import com.example.codechecker.data.mapper.UserMapper
import com.example.codechecker.domain.model.Role
import com.example.codechecker.domain.model.User
import com.example.codechecker.domain.model.UserStatus
import com.example.codechecker.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of UserRepository
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun registerUser(user: User, passwordHash: String): Long {
        val userEntity = UserMapper.toEntity(user, passwordHash)
        return userDao.insertUser(userEntity)
    }

    override suspend fun login(username: String, passwordHash: String): User? {
        val userEntity = userDao.getUserByUsername(username) ?: return null

        // Check if user is active and not disabled
        if (!userEntity.isActive || userEntity.status == "DISABLED") {
            return null
        }

        if (userEntity.passwordHash != passwordHash) {
            return null
        }
        return UserMapper.toDomain(userEntity)
    }

    override suspend fun getUserById(userId: Long): User? {
        val userEntity = userDao.getUserById(userId) ?: return null
        return UserMapper.toDomain(userEntity)
    }

    override suspend fun getUserByUsername(username: String): User? {
        val userEntity = userDao.getUserByUsername(username) ?: return null
        return UserMapper.toDomain(userEntity)
    }

    override fun getAllUsersFlow(): Flow<List<User>> {
        return userDao.getAllUsersFlow().map { entities ->
            UserMapper.toDomainList(entities)
        }
    }

    override suspend fun updateUser(user: User) {
        val existing = userDao.getUserById(user.id) ?: return
        val updatedEntity = existing.copy(
            username = user.username,
            displayName = user.displayName,
            role = user.role.value,
            isActive = user.isActive,
            status = user.status.value
        )
        userDao.updateUser(updatedEntity)
    }

    override suspend fun updatePassword(userId: Long, newPasswordHash: String) {
        userDao.updateUserPasswordHash(userId, newPasswordHash)
    }

    // Admin operations

    override suspend fun getAllUsers(): List<User> {
        return UserMapper.toDomainList(userDao.getAllUsers())
    }

    override suspend fun getAllActiveUsers(): List<User> {
        return UserMapper.toDomainList(userDao.getAllActiveUsers())
    }

    override suspend fun updateUserStatus(userId: Long, status: UserStatus) {
        userDao.updateUserStatus(userId, status.value)
    }

    override suspend fun updateUserActiveFlag(userId: Long, isActive: Boolean) {
        userDao.updateUserActiveFlag(userId, isActive)
    }

    override suspend fun deleteUser(userId: Long) {
        userDao.updateUserActiveFlag(userId, false)
    }

    override suspend fun updateUserRole(userId: Long, role: Role) {
        userDao.updateUserRole(userId, role.value)
    }

    override suspend fun resetPassword(userId: Long, newPasswordHash: String) {
        userDao.updateUserPasswordHash(userId, newPasswordHash)
    }

    override suspend fun getUserCount(): Int {
        return userDao.getUserCount()
    }

    override suspend fun getActiveUserCount(): Int {
        return userDao.getActiveUserCount()
    }

    override suspend fun getUserCountByRole(role: Role): Int {
        return userDao.getUserCountByRole(role.value)
    }

    override suspend fun searchUsers(query: String): List<User> {
        return UserMapper.toDomainList(userDao.searchUsers(query))
    }

    override suspend fun getUsersPaged(limit: Int, offset: Int): List<User> {
        return UserMapper.toDomainList(userDao.getUsersPaged(limit, offset))
    }
}
