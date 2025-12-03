package com.example.codechecker.domain.usecase

import com.example.codechecker.domain.model.AdminSettings
import com.example.codechecker.domain.repository.AdminSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for getting admin settings
 */
@Singleton
class GetAdminSettingsUseCase @Inject constructor(
    private val adminSettingsRepository: AdminSettingsRepository
) {
    suspend operator fun invoke(): AdminSettings {
        return adminSettingsRepository.getAdminSettings()
    }

    fun getAdminSettingsFlow(): Flow<AdminSettings> {
        return adminSettingsRepository.getAdminSettingsFlow()
    }

    suspend fun getSetting(key: String): com.example.codechecker.domain.model.AdminSetting? {
        return adminSettingsRepository.getSetting(key)
    }

    suspend fun getAllSettings(): List<com.example.codechecker.domain.model.AdminSetting> {
        return adminSettingsRepository.getAllSettings()
    }
}
