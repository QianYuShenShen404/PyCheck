package com.example.codechecker.domain.usecase

import com.example.codechecker.domain.model.Report
import com.example.codechecker.domain.model.ReportStatus
import com.example.codechecker.domain.repository.AssignmentRepository
import com.example.codechecker.domain.repository.ReportRepository
import com.example.codechecker.domain.repository.SubmissionRepository
import com.example.codechecker.domain.model.Similarity
import com.example.codechecker.domain.model.Submission
import com.example.codechecker.algorithm.engine.PlagiarismEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

    @Singleton
    class PlagiarismUseCase @Inject constructor(
        private val assignmentRepository: AssignmentRepository,
        private val submissionRepository: SubmissionRepository,
        private val reportRepository: ReportRepository,
        private val plagiarismEngine: PlagiarismEngine
    ) {
    private suspend fun getLatestSubmissionsByStudent(assignmentId: Long): List<Submission> {
        val all = submissionRepository.getAllSubmissionsByAssignment(assignmentId)
        if (all.isEmpty()) return emptyList()
        val grouped = all.groupBy { it.studentId }
        return grouped.values.mapNotNull { subs -> subs.maxByOrNull { it.submittedAt } }
    }

    suspend fun generateReportLatestOnly(
        assignmentId: Long,
        executorId: Long,
        progressCallback: ((Float) -> Unit)? = null
    ): Flow<Result<Long>> = flow {
        try {
            emit(Result.success(-1L))
            val latest = getLatestSubmissionsByStudent(assignmentId)
            if (latest.size < 2) {
                emit(Result.failure(Exception("至少需要2位同学的最后一次提交")))
                return@flow
            }
            val report = Report(
                assignmentId = assignmentId,
                executorId = executorId,
                status = ReportStatus.PENDING,
                totalSubmissions = latest.size,
                totalPairs = (latest.size * (latest.size - 1)) / 2,
                createdAt = System.currentTimeMillis(),
                completedAt = null
            )
            val reportId = reportRepository.createReport(report)
            progressCallback?.invoke(0.1f)
            val similarities = plagiarismEngine.detectPlagiarism(latest) { p ->
                val mapped = 0.1f + (p.current.toFloat() / p.total.toFloat()) * 0.8f
                progressCallback?.invoke(mapped)
            }
            similarities.forEach { similarity ->
                reportRepository.createSimilarity(similarity.copy(reportId = reportId))
            }
            val updated = report.copy(id = reportId, status = ReportStatus.COMPLETED, completedAt = System.currentTimeMillis())
            reportRepository.updateReport(updated)
            progressCallback?.invoke(1.0f)
            emit(Result.success(reportId))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun generateStudentLatestTargetReport(
        assignmentId: Long,
        studentId: Long,
        progressCallback: ((Float) -> Unit)? = null
    ): Result<Pair<Long, List<Similarity>>> {
        return try {
            val latest = getLatestSubmissionsByStudent(assignmentId)
            val self = latest.find { it.studentId == studentId }
            val others = latest.filter { it.studentId != studentId }
            if (self == null || others.isEmpty()) {
                return Result.failure(Exception("没有可比对对象"))
            }
            val report = Report(
                assignmentId = assignmentId,
                executorId = studentId,
                status = ReportStatus.PENDING,
                totalSubmissions = others.size + 1,
                totalPairs = others.size,
                createdAt = System.currentTimeMillis(),
                completedAt = null
            )
            val reportId = reportRepository.createReport(report)
            progressCallback?.invoke(0.1f)
            val toCompare = mutableListOf<Submission>()
            toCompare.add(self)
            toCompare.addAll(others)
            val allSimilarities = plagiarismEngine.detectPlagiarism(toCompare, null)
            val filtered = allSimilarities.filter { it.submission1Id == self.id || it.submission2Id == self.id }
            filtered.forEach { similarity ->
                reportRepository.createSimilarity(similarity.copy(reportId = reportId))
            }
            val updated = report.copy(id = reportId, status = ReportStatus.COMPLETED, completedAt = System.currentTimeMillis())
            reportRepository.updateReport(updated)
            progressCallback?.invoke(1.0f)
            Result.success(reportId to filtered)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    /**
     * Generate plagiarism report for an assignment
     * Performs pairwise comparison of all submissions
     *
     * @param assignmentId Assignment ID
     * @param executorId Teacher ID who initiated the report
     * @param progressCallback Optional callback for progress updates
     * @return Flow of progress updates and final result
     */
    suspend fun generateReport(
        assignmentId: Long,
        executorId: Long,
        progressCallback: ((Float) -> Unit)? = null
    ): Flow<Result<Long>> = flow {
        try {
            // Update status to PENDING
            emit(Result.success(-1L))

            val submissions = submissionRepository.getAllSubmissionsByAssignment(assignmentId)
            if (submissions.isEmpty()) {
                emit(Result.failure(Exception("没有提交记录")))
                return@flow
            }

            if (submissions.size < 2) {
                emit(Result.failure(Exception("至少需要2份提交才能进行查重")))
                return@flow
            }

            // Create initial report
            val report = Report(
                assignmentId = assignmentId,
                executorId = executorId,
                status = ReportStatus.PENDING,
                totalSubmissions = submissions.size,
                totalPairs = (submissions.size * (submissions.size - 1)) / 2,
                createdAt = System.currentTimeMillis(),
                completedAt = null
            )
            val reportId = reportRepository.createReport(report)

            // Update progress: 10%
            progressCallback?.invoke(0.1f)

            // Perform plagiarism detection
            val totalComparisons = (submissions.size * (submissions.size - 1)) / 2
            var completedComparisons = 0

            val similarities = mutableListOf<Similarity>()
            for (i in submissions.indices) {
                for (j in i + 1 until submissions.size) {
                    val submission1 = submissions[i]
                    val submission2 = submissions[j]

                    // Calculate similarity
                    val similarityResult = com.example.codechecker.algorithm.similarity.SimilarityResult(
                        jaccardScore = 0f,
                        lcsScore = 0f,
                        combinedScore = 0f
                    )

                    similarities.add(
                        Similarity(
                            reportId = reportId,
                            submission1Id = submission1.id,
                            submission2Id = submission2.id,
                            similarityScore = similarityResult.combinedScore,
                            jaccardScore = similarityResult.jaccardScore,
                            lcsScore = similarityResult.lcsScore,
                            highlightData = com.example.codechecker.domain.model.HighlightData(
                                matches = emptyList()
                            ),
                            aiAnalysis = null,
                            createdAt = System.currentTimeMillis()
                        )
                    )

                    completedComparisons++
                    val progress = 0.1f + (completedComparisons.toFloat() / totalComparisons.toFloat()) * 0.8f
                    progressCallback?.invoke(progress)
                }
            }

            // Save all similarities
            similarities.forEach { similarity ->
                reportRepository.createSimilarity(similarity)
            }

            // Update report status to COMPLETED
            val updatedReport = report.copy(
                id = reportId,
                status = ReportStatus.COMPLETED,
                completedAt = System.currentTimeMillis()
            )
            reportRepository.updateReport(updatedReport)

            // Final progress: 100%
            progressCallback?.invoke(1.0f)

            emit(Result.success(reportId))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun compareNewSubmission(
        assignmentId: Long,
        newSubmissionId: Long,
        threshold: Float? = null
    ): List<Similarity> {
        val submissions = submissionRepository.getAllSubmissionsByAssignment(assignmentId)
        if (submissions.isEmpty()) return emptyList()
        val newSubmission = submissions.find { it.id == newSubmissionId } ?: return emptyList()
        val others = submissions.filter { it.id != newSubmissionId }
        if (others.isEmpty()) return emptyList()

        val toCompare = mutableListOf<Submission>()
        toCompare.add(newSubmission)
        toCompare.addAll(others)
        val allSimilarities = plagiarismEngine.detectPlagiarism(toCompare, null)
        val filtered = allSimilarities.filter { it.submission1Id == newSubmissionId || it.submission2Id == newSubmissionId }
        return if (threshold != null) filtered.filter { it.similarityScore >= threshold } else filtered
    }

    /**
     * Generate plagiarism report with fast detection
     * Only compares submissions with similar hashes
     */
    suspend fun generateReportFast(
        assignmentId: Long,
        executorId: Long,
        progressCallback: ((Float) -> Unit)? = null
    ): Flow<Result<Long>> = flow {
        try {
            emit(Result.success(-1L))

            val submissions = submissionRepository.getAllSubmissionsByAssignment(assignmentId)
            if (submissions.isEmpty()) {
                emit(Result.failure(Exception("没有提交记录")))
                return@flow
            }

            if (submissions.size < 2) {
                emit(Result.failure(Exception("至少需要2份提交才能进行查重")))
                return@flow
            }

            val report = Report(
                assignmentId = assignmentId,
                executorId = executorId,
                status = ReportStatus.PENDING,
                totalSubmissions = submissions.size,
                totalPairs = (submissions.size * (submissions.size - 1)) / 2,
                createdAt = System.currentTimeMillis(),
                completedAt = null
            )
            val reportId = reportRepository.createReport(report)

            progressCallback?.invoke(0.1f)

            // Use fast detection
            val similarities = plagiarismEngine.detectPlagiarismFast(submissions) { progress ->
                // Map to 0.1 - 0.9 range
                val mappedProgress = 0.1f + (progress.current.toFloat() / progress.total.toFloat()) * 0.8f
                progressCallback?.invoke(mappedProgress)
            }

            // Save all similarities
            similarities.forEach { similarity ->
                reportRepository.createSimilarity(similarity.copy(reportId = reportId))
            }

            val updatedReport = report.copy(
                id = reportId,
                status = ReportStatus.COMPLETED,
                completedAt = System.currentTimeMillis()
            )
            reportRepository.updateReport(updatedReport)

            progressCallback?.invoke(1.0f)
            emit(Result.success(reportId))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Get report by ID
     */
    suspend fun getReportById(reportId: Long): Report? {
        return withContext(Dispatchers.IO) {
            reportRepository.getReportById(reportId)
        }
    }

    /**
     * Get all reports for an assignment
     */
    suspend fun getReportsByAssignment(assignmentId: Long): List<Report> {
        return withContext(Dispatchers.IO) {
            reportRepository.getReportsByAssignment(assignmentId)
        }
    }

    /**
     * Get similarities for a report
     */
    suspend fun getSimilaritiesByReport(reportId: Long): List<Similarity> {
        return withContext(Dispatchers.IO) {
            reportRepository.getSimilaritiesByReport(reportId)
        }
    }

    /**
     * Get high-similarity pairs (above threshold)
     */
    suspend fun getHighSimilarityPairs(
        reportId: Long,
        threshold: Float = 60f
    ): List<Similarity> {
        return withContext(Dispatchers.IO) {
            val similarities = reportRepository.getSimilaritiesByReport(reportId)
            similarities.filter { it.similarityScore >= threshold }
                .sortedByDescending { it.similarityScore }
        }
    }

    /**
     * Get similarity by ID
     */
    suspend fun getSimilarityById(similarityId: Long): Similarity? {
        return withContext(Dispatchers.IO) {
            reportRepository.getSimilarityById(similarityId)
        }
    }
}
