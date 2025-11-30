package com.example.codechecker.data.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.codechecker.domain.model.Role
import com.example.codechecker.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataStore name
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_session")

/**
 * Manager for user session persistence using DataStore
 */
@Singleton
class UserSessionManager @Inject constructor(
    private val context: Context
) {

    /**
     * Get current user session
     */
    val currentUser: Flow<User?> = context.dataStore.data.map { preferences ->
        val userId = preferences[PreferenceKeys.USER_ID] ?: return@map null
        val username = preferences[PreferenceKeys.USERNAME] ?: return@map null
        val displayName = preferences[PreferenceKeys.DISPLAY_NAME] ?: return@map null
        val role = preferences[PreferenceKeys.USER_ROLE] ?: return@map null
        val createdAt = preferences[PreferenceKeys.LOGIN_TIME] ?: 0L

        User(
            id = userId,
            username = username,
            displayName = displayName,
            role = Role.fromValue(role),
            createdAt = createdAt
        )
    }

    /**
     * Get current user as nullable
     */
    suspend fun getCurrentUser(): User? {
        return currentUser.firstOrNull()
    }

    /**
     * Save user session
     */
    suspend fun saveUserSession(user: User) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.USER_ID] = user.id
            preferences[PreferenceKeys.USERNAME] = user.username
            preferences[PreferenceKeys.DISPLAY_NAME] = user.displayName
            preferences[PreferenceKeys.USER_ROLE] = user.role.value
            preferences[PreferenceKeys.LOGIN_TIME] = System.currentTimeMillis()
        }
    }

    suspend fun setEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.EMAIL] = email
        }
    }

    suspend fun getEmail(): String? {
        return context.dataStore.data.map { it[PreferenceKeys.EMAIL] }.firstOrNull()
    }

    /**
     * Clear user session (logout)
     */
    suspend fun clearUserSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * Check if user is logged in
     */
    suspend fun isLoggedIn(): Boolean {
        return getCurrentUser() != null
    }

    companion object {
        const val SESSION_TIMEOUT_MS = 24 * 60 * 60 * 1000L // 24 hours
    }
}
