package com.example.codechecker.domain.repository

import com.example.codechecker.domain.model.AdminSetting
import com.example.codechecker.domain.model.AdminSettings
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for AdminSettings operations
 */
interface AdminSettingsRepository {

    /**
     * Get all admin settings as AdminSettings object
     */
    suspend fun getAdminSettings(): AdminSettings

    /**
     * Get all admin settings as Flow
     */
    fun getAdminSettingsFlow(): Flow<AdminSettings>

    /**
     * Update admin settings
     */
    suspend fun updateAdminSettings(settings: AdminSettings, updatedBy: Long): Unit

    /**
     * Get individual setting by key
     */
    suspend fun getSetting(key: String): AdminSetting?

    /**
     * Update individual setting
     */
    suspend fun updateSetting(key: String, value: String, type: com.example.codechecker.domain.model.SettingType, updatedBy: Long): Unit

    /**
     * Get all settings as list
     */
    suspend fun getAllSettings(): List<AdminSetting>

    /**
     * Reset to default settings
     */
    suspend fun resetToDefaults(updatedBy: Long): Unit

    /**
     * Export settings as JSON string
     */
    suspend fun exportSettings(): String

    /**
     * Import settings from JSON string
     */
    suspend fun importSettings(json: String, updatedBy: Long): Unit
}
