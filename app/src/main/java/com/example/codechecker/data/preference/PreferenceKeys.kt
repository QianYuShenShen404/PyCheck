package com.example.codechecker.data.preference

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * DataStore preference keys
 */
object PreferenceKeys {

    /**
     * Currently logged in user ID
     */
    val USER_ID: Preferences.Key<Long> = longPreferencesKey("user_id")

    /**
     * Currently logged in username
     */
    val USERNAME: Preferences.Key<String> = stringPreferencesKey("username")

    /**
     * Currently logged in user's display name
     */
    val DISPLAY_NAME: Preferences.Key<String> = stringPreferencesKey("display_name")

    /**
     * Currently logged in user's role
     */
    val USER_ROLE: Preferences.Key<String> = stringPreferencesKey("user_role")

    /**
     * Login timestamp
     */
    val LOGIN_TIME: Preferences.Key<Long> = longPreferencesKey("login_time")

    val EMAIL: Preferences.Key<String> = stringPreferencesKey("email")

    /**
     * User is active status
     */
    val USER_IS_ACTIVE: Preferences.Key<Boolean> = androidx.datastore.preferences.core.booleanPreferencesKey("user_is_active")

    /**
     * User status (ACTIVE/DISABLED)
     */
    val USER_STATUS: Preferences.Key<String> = stringPreferencesKey("user_status")
}
