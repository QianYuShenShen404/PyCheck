package com.example.codechecker.domain.model

sealed class AIAnalysisResult {
    data class Success(
        val reason: String,
        val isCommonCode: Boolean,
        val plagiarismRisk: String,
        val analysis: String
    ) : AIAnalysisResult()

    data class Error(val message: String) : AIAnalysisResult()
}

