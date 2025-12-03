package com.example.codechecker.algorithm.service

import com.example.codechecker.domain.repository.AdminSettingsRepository
import com.example.codechecker.domain.model.AdminSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for managing algorithm settings from admin configuration
 */
@Singleton
class AlgorithmSettingsService @Inject constructor(
    private val adminSettingsRepository: AdminSettingsRepository
) {

    /**
     * Get current algorithm settings
     */
    suspend fun getAlgorithmSettings(): AlgorithmSettings {
        val adminSettings = adminSettingsRepository.getAdminSettings()
        return AlgorithmSettings(
            similarityThreshold = adminSettings.similarityThreshold,
            fastCompareMode = adminSettings.fastCompareMode
        )
    }

    /**
     * Get algorithm settings as Flow
     */
    fun getAlgorithmSettingsFlow(): Flow<AlgorithmSettings> {
        return adminSettingsRepository.getAdminSettingsFlow().map { adminSettings ->
            AlgorithmSettings(
                similarityThreshold = adminSettings.similarityThreshold,
                fastCompareMode = adminSettings.fastCompareMode
            )
        }
    }

    /**
     * Get similarity threshold
     */
    suspend fun getSimilarityThreshold(): Float {
        return adminSettingsRepository.getAdminSettings().similarityThreshold.toFloat()
    }

    /**
     * Check if fast compare mode is enabled
     */
    suspend fun isFastCompareModeEnabled(): Boolean {
        return adminSettingsRepository.getAdminSettings().fastCompareMode
    }

    /**
     * Check if similarity score exceeds threshold
     */
    suspend fun isHighSimilarity(score: Float): Boolean {
        return score >= getSimilarityThreshold()
    }

    /**
     * Get similarity threshold for high similarity warning
     */
    suspend fun getHighSimilarityThreshold(): Float {
        return getSimilarityThreshold()
    }
}

/**
 * Algorithm-specific settings
 */
data class AlgorithmSettings(
    val similarityThreshold: Int,
    val fastCompareMode: Boolean
)
