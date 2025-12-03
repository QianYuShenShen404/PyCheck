package com.example.codechecker.domain.repository

import com.example.codechecker.domain.model.AIAnalysisResult

interface AIRepository {
    suspend fun analyze(code1: String, code2: String, similarity: Double): AIAnalysisResult
}

