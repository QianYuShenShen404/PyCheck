package com.example.codechecker.data.local.dao

import androidx.room.*
import com.example.codechecker.data.local.entity.AdminSettingEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for AdminSetting entity
 */
@Dao
interface AdminSettingDao {

    /**
     * Insert or update a setting
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: AdminSettingEntity)

    /**
     * Insert or update multiple settings
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: List<AdminSettingEntity>)

    /**
     * Update a setting
     */
    @Update
    suspend fun updateSetting(setting: AdminSettingEntity)

    /**
     * Delete a setting
     */
    @Delete
    suspend fun deleteSetting(setting: AdminSettingEntity)

    /**
     * Get setting by key
     */
    @Query("SELECT * FROM admin_settings WHERE `key` = :key")
    suspend fun getSettingByKey(key: String): AdminSettingEntity?

    /**
     * Get all settings
     */
    @Query("SELECT * FROM admin_settings")
    suspend fun getAllSettings(): List<AdminSettingEntity>

    /**
     * Get all settings as Flow
     */
    @Query("SELECT * FROM admin_settings")
    fun getAllSettingsFlow(): Flow<List<AdminSettingEntity>>

    /**
     * Delete all settings
     */
    @Query("DELETE FROM admin_settings")
    suspend fun deleteAllSettings()

    /**
     * Get settings by type
     */
    @Query("SELECT * FROM admin_settings WHERE type = :type")
    suspend fun getSettingsByType(type: String): List<AdminSettingEntity>

    /**
     * Get settings count
     */
    @Query("SELECT COUNT(*) FROM admin_settings")
    suspend fun getSettingsCount(): Int
}
