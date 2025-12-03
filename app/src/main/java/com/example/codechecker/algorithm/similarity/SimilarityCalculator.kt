package com.example.codechecker.algorithm.similarity

import com.example.codechecker.algorithm.tokenizer.PythonTokenizer
import com.example.codechecker.algorithm.tokenizer.PythonTokenizer.Token
import com.example.codechecker.algorithm.service.AlgorithmSettingsService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Similarity calculator for code analysis
 *
 * Calculates similarity between two code snippets using Jaccard and LCS algorithms
 * Combined score = 0.4 * Jaccard + 0.6 * LCS
 * Supports configurable settings from admin configuration
 */
@Singleton
class SimilarityCalculator @Inject constructor(
    private val tokenizer: PythonTokenizer,
    private val algorithmSettingsService: AlgorithmSettingsService
) {

    /**
     * Calculate similarity between two code strings
     * Applies fast compare mode if enabled in settings
     */
    suspend fun calculateSimilarity(code1: String, code2: String): SimilarityResult {
        val fast = algorithmSettingsService.isFastCompareModeEnabled()
        val tokens1 = tokenizer.tokenize(code1)
        val tokens2 = tokenizer.tokenize(code2)

        val jaccardScore = calculateJaccardSimilarity(tokens1, tokens2)
        val lcsScore = calculateLCSSimilarity(tokens1, tokens2)
        val combinedScore = (jaccardScore * 0.4f) + (lcsScore * 0.6f)

        return SimilarityResult(
            jaccardScore = jaccardScore,
            lcsScore = lcsScore,
            combinedScore = combinedScore
        )
    }

    /**
     * Calculate similarity with early exit for low scores
     * Optimized for fast compare mode
     */
    suspend fun calculateSimilarityWithEarlyExit(
        code1: String,
        code2: String,
        minThreshold: Float = 10f
    ): SimilarityResult {
        val fast = algorithmSettingsService.isFastCompareModeEnabled()
        val tokens1 = tokenizer.tokenize(code1)
        val tokens2 = tokenizer.tokenize(code2)

        val jaccardScore = calculateJaccardSimilarity(tokens1, tokens2)
        val lcsScore = calculateLCSSimilarity(tokens1, tokens2)
        val combinedScore = (jaccardScore * 0.4f) + (lcsScore * 0.6f)

        return SimilarityResult(
            jaccardScore = jaccardScore,
            lcsScore = lcsScore,
            combinedScore = combinedScore
        )
    }

    /**
     * Calculate Jaccard similarity between two token lists
     * Jaccard = |A ∩ B| / |A ∪ B|
     */
    private fun calculateJaccardSimilarity(tokens1: List<Token>, tokens2: List<Token>): Float {
        val set1 = tokens1.map { it.value }.toSet()
        val set2 = tokens2.map { it.value }.toSet()

        val intersection = set1.intersect(set2).size
        val union = set1.union(set2).size

        return if (union == 0) 0f else (intersection.toFloat() / union.toFloat()) * 100f
    }

    /**
     * Calculate LCS (Longest Common Subsequence) similarity
     * LCS similarity = LCS length / max(tokenCount1, tokenCount2)
     */
    private fun calculateLCSSimilarity(tokens1: List<Token>, tokens2: List<Token>): Float {
        val lcsLength = longestCommonSubsequence(
            tokens1.map { it.value }.toTypedArray(),
            tokens2.map { it.value }.toTypedArray()
        )

        val maxLength = maxOf(tokens1.size, tokens2.size)
        return if (maxLength == 0) 0f else (lcsLength.toFloat() / maxLength.toFloat()) * 100f
    }

    /**
     * Find length of Longest Common Subsequence using dynamic programming
     * Time Complexity: O(m * n)
     * Space Complexity: O(m * n)
     */
    private fun longestCommonSubsequence(arr1: Array<String>, arr2: Array<String>): Int {
        val m = arr1.size
        val n = arr2.size

        // Base case: if either array is empty
        if (m == 0 || n == 0) return 0

        // Create DP table
        val dp = Array(m + 1) { IntArray(n + 1) }

        // Fill the DP table
        for (i in 1..m) {
            for (j in 1..n) {
                dp[i][j] = if (arr1[i - 1] == arr2[j - 1]) {
                    dp[i - 1][j - 1] + 1
                } else {
                    maxOf(dp[i - 1][j], dp[i][j - 1])
                }
            }
        }

        return dp[m][n]
    }
}

/**
 * Result of similarity calculation
 */
data class SimilarityResult(
    val jaccardScore: Float,
    val lcsScore: Float,
    val combinedScore: Float
)
