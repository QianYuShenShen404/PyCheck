package com.example.codechecker.data.mapper

import com.example.codechecker.data.local.entity.SimilarityEntity
import com.example.codechecker.domain.model.AIAnalysis
import com.example.codechecker.domain.model.HighlightData
import com.example.codechecker.domain.model.MatchType
import com.example.codechecker.domain.model.MatchRegion
import com.example.codechecker.domain.model.RiskLevel
import com.example.codechecker.domain.model.Similarity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Mapper for Similarity entity and domain model
 */
object SimilarityMapper {
    
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Convert entity to domain model
     */
    fun toDomain(entity: SimilarityEntity): Similarity {
        return Similarity(
            id = entity.id,
            reportId = entity.reportId,
            submission1Id = entity.submission1Id,
            submission2Id = entity.submission2Id,
            similarityScore = entity.similarityScore,
            jaccardScore = entity.jaccardScore,
            lcsScore = entity.lcsScore,
            highlightData = parseHighlightData(entity.highlightData),
            aiAnalysis = parseAIAnalysis(entity.aiAnalysis),
            createdAt = entity.createdAt
        )
    }

    /**
     * Convert domain model to entity
     */
    fun toEntity(domain: Similarity): SimilarityEntity {
        return SimilarityEntity(
            id = domain.id,
            reportId = domain.reportId,
            submission1Id = domain.submission1Id,
            submission2Id = domain.submission2Id,
            similarityScore = domain.similarityScore,
            jaccardScore = domain.jaccardScore,
            lcsScore = domain.lcsScore,
            highlightData = formatHighlightData(domain.highlightData),
            aiAnalysis = formatAIAnalysis(domain.aiAnalysis),
            createdAt = domain.createdAt
        )
    }

    /**
     * Convert list of entities to domain models
     */
    fun toDomainList(entities: List<SimilarityEntity>): List<Similarity> {
        return entities.map { toDomain(it) }
    }

    private fun parseHighlightData(jsonString: String): HighlightData {
        return try {
            json.decodeFromString<HighlightData>(jsonString)
        } catch (e: Exception) {
            HighlightData(emptyList())
        }
    }

    private fun formatHighlightData(highlightData: HighlightData): String {
        return try {
            json.encodeToString(highlightData)
        } catch (e: Exception) {
            ""
        }
    }

    private fun parseAIAnalysis(jsonString: String?): AIAnalysis? {
        return jsonString?.let { jsonStr ->
            try {
                json.decodeFromString<AIAnalysis>(jsonStr)
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun formatAIAnalysis(aiAnalysis: AIAnalysis?): String? {
        return aiAnalysis?.let { analysis ->
            try {
                json.encodeToString(analysis)
            } catch (e: Exception) {
                null
            }
        }
    }
}
