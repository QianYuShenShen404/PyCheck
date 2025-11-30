package com.example.codechecker.algorithm.engine

import com.example.codechecker.domain.model.Submission
import com.example.codechecker.domain.model.Similarity
import com.example.codechecker.domain.model.HighlightData
import com.example.codechecker.domain.model.MatchRegion
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Plagiarism detection engine
 *
 * Compares all submissions pairwise and calculates similarity scores
 */
@Singleton
class PlagiarismEngine @Inject constructor(
    private val tokenizer: com.example.codechecker.algorithm.tokenizer.PythonTokenizer,
    private val similarityCalculator: com.example.codechecker.algorithm.similarity.SimilarityCalculator
) {
    data class PlagiarismProgress(
        val current: Int,
        val total: Int
    )

    /**
     * Detect plagiarism among submissions
     * Performs pairwise comparison of all submissions
     *
     * @param submissions List of submissions to compare
     * @param progressCallback Optional callback to report progress
     * @return List of similarity results
     */
    suspend fun detectPlagiarism(
        submissions: List<Submission>,
        progressCallback: ((PlagiarismProgress) -> Unit)? = null
    ): List<Similarity> {
        val similarities = mutableListOf<Similarity>()
        val totalComparisons = (submissions.size * (submissions.size - 1)) / 2
        var currentComparison = 0

        for (i in submissions.indices) {
            for (j in i + 1 until submissions.size) {
                val submission1 = submissions[i]
                val submission2 = submissions[j]

                // Calculate similarity
                val similarityResult = similarityCalculator.calculateSimilarity(
                    submission1.codeContent,
                    submission2.codeContent
                )

                // Generate highlight data for matched regions
                val highlightData = generateHighlightData(
                    submission1.codeContent,
                    submission2.codeContent
                )

                similarities.add(
                    Similarity(
                        reportId = 0L, // Will be set by the caller
                        submission1Id = submission1.id,
                        submission2Id = submission2.id,
                        similarityScore = similarityResult.combinedScore,
                        jaccardScore = similarityResult.jaccardScore,
                        lcsScore = similarityResult.lcsScore,
                        highlightData = highlightData,
                        aiAnalysis = null,
                        createdAt = System.currentTimeMillis()
                    )
                )

                currentComparison++
                progressCallback?.invoke(PlagiarismProgress(currentComparison, totalComparisons))
            }
        }

        return similarities
    }

    /**
     * Detect plagiarism with limited scope (faster)
     * Only compares submissions with similar hashes
     */
    suspend fun detectPlagiarismFast(
        submissions: List<Submission>,
        progressCallback: ((PlagiarismProgress) -> Unit)? = null
    ): List<Similarity> {
        val similarities = mutableListOf<Similarity>()

        // Group submissions by hash prefix for quick filtering
        val hashGroups = submissions.groupBy { it.codeHash.take(8) }

        var processed = 0
        val total = hashGroups.size

        for ((_, group) in hashGroups) {
            if (group.size < 2) continue

            val totalComparisons = (group.size * (group.size - 1)) / 2
            var currentComparison = 0

            for (i in group.indices) {
                for (j in i + 1 until group.size) {
                    val submission1 = group[i]
                    val submission2 = group[j]

                    val similarityResult = similarityCalculator.calculateSimilarity(
                        submission1.codeContent,
                        submission2.codeContent
                    )

                    // Skip low-similarity pairs
                    if (similarityResult.combinedScore < 10f) {
                        currentComparison++
                        processed++
                        progressCallback?.invoke(PlagiarismProgress(processed, total))
                        continue
                    }

                    val highlightData = generateHighlightData(
                        submission1.codeContent,
                        submission2.codeContent
                    )

                    similarities.add(
                        Similarity(
                            reportId = 0L,
                            submission1Id = submission1.id,
                            submission2Id = submission2.id,
                            similarityScore = similarityResult.combinedScore,
                            jaccardScore = similarityResult.jaccardScore,
                            lcsScore = similarityResult.lcsScore,
                            highlightData = highlightData,
                            aiAnalysis = null,
                            createdAt = System.currentTimeMillis()
                        )
                    )

                    currentComparison++
                }
            }
            processed++
            progressCallback?.invoke(PlagiarismProgress(processed, total))
        }

        return similarities
    }

    /**
     * Generate highlight data for matched regions
     * This is a simplified implementation
     */
    private fun generateHighlightData(
        code1: String,
        code2: String
    ): HighlightData {
        val tokens1 = tokenizer.tokenize(code1)
        val tokens2 = tokenizer.tokenize(code2)

        val commonTokens1 = tokens1.filter { token1 ->
            tokens2.any { token2 -> token2.value == token1.value }
        }.map { it.value }

        val commonTokens2 = tokens2.filter { token2 ->
            tokens1.any { token1 -> token1.value == token2.value }
        }.map { it.value }

        val matchRegions = mutableListOf<MatchRegion>()

        // Simple highlight: mark lines containing common tokens
        val lines1 = code1.lines()
        val lines2 = code2.lines()

        for ((lineIndex1, line1) in lines1.withIndex()) {
            if (commonTokens1.any { line1.contains(it) }) {
                // Find matching lines in code2
                for ((lineIndex2, line2) in lines2.withIndex()) {
                    if (commonTokens2.any { line2.contains(it) } && line1.trim() == line2.trim()) {
                        matchRegions.add(
                            MatchRegion(
                                submission1LineStart = lineIndex1,
                                submission1LineEnd = lineIndex1,
                                submission2LineStart = lineIndex2,
                                submission2LineEnd = lineIndex2,
                                matchType = com.example.codechecker.domain.model.MatchType.EXACT_MATCH
                            )
                        )
                    }
                }
            }
        }

        return HighlightData(
            matches = matchRegions
        )
    }

    /**
     * Find high-similarity pairs (threshold-based filtering)
     */
    suspend fun findHighSimilarityPairs(
        submissions: List<Submission>,
        threshold: Float = 60f,
        progressCallback: ((PlagiarismProgress) -> Unit)? = null
    ): List<Similarity> {
        val allSimilarities = detectPlagiarism(submissions, progressCallback)

        // Filter by threshold
        return allSimilarities.filter { it.similarityScore >= threshold }
            .sortedByDescending { it.similarityScore }
    }
}
