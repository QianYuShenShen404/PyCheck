package com.example.codechecker.data.repository

import com.example.codechecker.data.local.dao.UserDao
import com.example.codechecker.data.mapper.UserMapper
import com.example.codechecker.domain.model.User
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
        val userEntity = UserMapper.toEntity(user).copy(passwordHash = passwordHash)
        return userDao.insertUser(userEntity)
    }

    override suspend fun login(username: String, passwordHash: String): User? {
        val userEntity = userDao.getUserByUsername(username) ?: return null
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
            role = user.role.value
        )
        userDao.updateUser(updatedEntity)
    }

    override suspend fun updatePassword(userId: Long, newPasswordHash: String) {
        val existing = userDao.getUserById(userId) ?: return
        val updatedEntity = existing.copy(passwordHash = newPasswordHash)
        userDao.updateUser(updatedEntity)
    }
}
