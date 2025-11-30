package com.example.codechecker.domain.model

/**
 * Domain model for Similarity
 */
data class Similarity(
    val id: Long = 0,
    val reportId: Long,
    val submission1Id: Long,
    val submission2Id: Long,
    val similarityScore: Float,
    val jaccardScore: Float,
    val lcsScore: Float,
    val highlightData: HighlightData,
    val aiAnalysis: AIAnalysis?,
    val createdAt: Long
)

/**
 * Highlight data for code comparison
 */
data class HighlightData(
    val matches: List<MatchRegion>
)

/**
 * Match region in code comparison
 */
data class MatchRegion(
    val submission1LineStart: Int,
    val submission1LineEnd: Int,
    val submission2LineStart: Int,
    val submission2LineEnd: Int,
    val matchType: MatchType
)

/**
 * Type of match
 */
enum class MatchType(val value: String) {
    EXACT_MATCH("EXACT_MATCH"),
    STRUCTURAL_MATCH("STRUCTURAL_MATCH");

    companion object {
        fun fromValue(value: String): MatchType {
            return values().find { it.value == value } ?: EXACT_MATCH
        }
    }
}

/**
 * AI analysis result
 */
data class AIAnalysis(
    val similarityReason: String,
    val riskLevel: RiskLevel,
    val explanation: String
)

/**
 * Risk level enum
 */
enum class RiskLevel(val value: String) {
    LOW("LOW"),
    MEDIUM("MEDIUM"),
    HIGH("HIGH");

    companion object {
        fun fromValue(value: String): RiskLevel {
            return values().find { it.value == value } ?: LOW
        }
    }
}
