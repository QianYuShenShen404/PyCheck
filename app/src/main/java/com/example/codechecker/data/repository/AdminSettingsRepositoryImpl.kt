package com.example.codechecker.data.repository

import com.example.codechecker.data.local.dao.AdminSettingDao
import com.example.codechecker.data.local.entity.AdminSettingEntity
import com.example.codechecker.data.mapper.AdminSettingMapper
import com.example.codechecker.domain.model.AdminSettings
import com.example.codechecker.domain.model.AdminSetting
import com.example.codechecker.domain.model.LogLevel
import com.example.codechecker.domain.model.SettingType
import com.example.codechecker.domain.repository.AdminSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AdminSettingsRepository
 */
@Singleton
class AdminSettingsRepositoryImpl @Inject constructor(
    private val adminSettingDao: AdminSettingDao
) : AdminSettingsRepository {

    private val json = Json { encodeDefaults = true; ignoreUnknownKeys = true }

    override suspend fun getAdminSettings(): AdminSettings {
        val entities = adminSettingDao.getAllSettings()
        val settings = AdminSettingMapper.toDomainList(entities)
        return mapSettingsToDomain(settings)
    }

    override fun getAdminSettingsFlow(): Flow<AdminSettings> {
        return adminSettingDao.getAllSettingsFlow().map { entities ->
            val settings = AdminSettingMapper.toDomainList(entities)
            mapSettingsToDomain(settings)
        }
    }

    override suspend fun updateAdminSettings(settings: AdminSettings, updatedBy: Long) {
        val entities = mapDomainToSettings(settings, updatedBy)
        adminSettingDao.insertSettings(entities)
    }

    override suspend fun getSetting(key: String): AdminSetting? {
        val entity = adminSettingDao.getSettingByKey(key) ?: return null
        return AdminSettingMapper.toDomain(entity)
    }

    override suspend fun updateSetting(
        key: String,
        value: String,
        type: SettingType,
        updatedBy: Long
    ) {
        val setting = AdminSetting(
            key = key,
            value = value,
            type = type,
            updatedAt = System.currentTimeMillis(),
            updatedBy = updatedBy
        )
        val entity = AdminSettingMapper.toEntity(setting)
        adminSettingDao.insertSetting(entity)
    }

    override suspend fun getAllSettings(): List<AdminSetting> {
        val entities = adminSettingDao.getAllSettings()
        return AdminSettingMapper.toDomainList(entities)
    }

    override suspend fun resetToDefaults(updatedBy: Long) {
        adminSettingDao.deleteAllSettings()
        val defaults = getDefaultSettings(updatedBy)
        adminSettingDao.insertSettings(defaults)
    }

    override suspend fun exportSettings(): String {
        val settings = getAllSettings()
        return json.encodeToString(settings)
    }

    override suspend fun importSettings(jsonString: String, updatedBy: Long) {
        val settings: List<AdminSetting> = try {
            json.decodeFromString(jsonString)
        } catch (e: Exception) {
            throw IllegalArgumentException("无效的设置格式")
        }

        val entities = settings.map { setting ->
            AdminSettingMapper.toEntity(
                setting.copy(
                    updatedAt = System.currentTimeMillis(),
                    updatedBy = updatedBy
                )
            )
        }

        adminSettingDao.insertSettings(entities)
    }

    private fun mapSettingsToDomain(settings: List<AdminSetting>): AdminSettings {
        val settingMap = settings.associateBy { it.key }
        return AdminSettings(
            similarityThreshold = settingMap["similarity_threshold"]?.value?.toIntOrNull() ?: 60,
            reportRetentionDays = settingMap["report_retention_days"]?.value?.toIntOrNull() ?: 30,
            submissionRetentionDays = settingMap["submission_retention_days"]?.value?.toIntOrNull() ?: 180,
            fastCompareMode = settingMap["fast_compare_mode"]?.value?.toBooleanStrictOrNull() ?: false,
            logLevel = settingMap["log_level"]?.value?.let { LogLevel.fromValue(it) } ?: LogLevel.INFO,
            autoCleanupEnabled = settingMap["auto_cleanup_enabled"]?.value?.toBooleanStrictOrNull() ?: false,
            maxSubmissionsPerAssignment = settingMap["max_submissions_per_assignment"]?.value?.toIntOrNull() ?: 200
        )
    }

    private fun mapDomainToSettings(settings: AdminSettings, updatedBy: Long): List<AdminSettingEntity> {
        val currentTime = System.currentTimeMillis()
        return listOf(
            AdminSettingEntity("similarity_threshold", settings.similarityThreshold.toString(), "INT", currentTime, updatedBy),
            AdminSettingEntity("report_retention_days", settings.reportRetentionDays.toString(), "INT", currentTime, updatedBy),
            AdminSettingEntity("submission_retention_days", settings.submissionRetentionDays.toString(), "INT", currentTime, updatedBy),
            AdminSettingEntity("fast_compare_mode", settings.fastCompareMode.toString(), "BOOLEAN", currentTime, updatedBy),
            AdminSettingEntity("log_level", settings.logLevel.value, "STRING", currentTime, updatedBy),
            AdminSettingEntity("auto_cleanup_enabled", settings.autoCleanupEnabled.toString(), "BOOLEAN", currentTime, updatedBy),
            AdminSettingEntity("max_submissions_per_assignment", settings.maxSubmissionsPerAssignment.toString(), "INT", currentTime, updatedBy)
        )
    }

    private fun getDefaultSettings(updatedBy: Long): List<AdminSettingEntity> {
        val currentTime = System.currentTimeMillis()
        return listOf(
            AdminSettingEntity("similarity_threshold", "60", "INT", currentTime, updatedBy),
            AdminSettingEntity("report_retention_days", "30", "INT", currentTime, updatedBy),
            AdminSettingEntity("submission_retention_days", "180", "INT", currentTime, updatedBy),
            AdminSettingEntity("fast_compare_mode", "false", "BOOLEAN", currentTime, updatedBy),
            AdminSettingEntity("log_level", "INFO", "STRING", currentTime, updatedBy),
            AdminSettingEntity("auto_cleanup_enabled", "false", "BOOLEAN", currentTime, updatedBy),
            AdminSettingEntity("max_submissions_per_assignment", "200", "INT", currentTime, updatedBy)
        )
    }
}
