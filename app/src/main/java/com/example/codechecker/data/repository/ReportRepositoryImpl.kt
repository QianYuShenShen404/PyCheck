package com.example.codechecker.data.repository

import com.example.codechecker.data.local.dao.ReportDao
import com.example.codechecker.data.local.dao.SimilarityDao
import com.example.codechecker.data.mapper.ReportMapper
import com.example.codechecker.data.mapper.SimilarityMapper
import com.example.codechecker.domain.model.Report
import com.example.codechecker.domain.model.Similarity
import com.example.codechecker.domain.repository.ReportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ReportRepository
 */
@Singleton
class ReportRepositoryImpl @Inject constructor(
    private val reportDao: ReportDao,
    private val similarityDao: SimilarityDao
) : ReportRepository {

    override suspend fun createReport(report: Report): Long {
        val reportEntity = ReportMapper.toEntity(report)
        return reportDao.insertReport(reportEntity)
    }

    override suspend fun updateReport(report: Report) {
        val reportEntity = ReportMapper.toEntity(report)
        reportDao.updateReport(reportEntity)
    }

    override suspend fun deleteReport(report: Report) {
        val reportEntity = ReportMapper.toEntity(report)
        reportDao.deleteReport(reportEntity)
    }

    override suspend fun getReportById(reportId: Long): Report? {
        val reportEntity = reportDao.getReportById(reportId) ?: return null
        return ReportMapper.toDomain(reportEntity)
    }

    override fun getReportsByAssignmentFlow(assignmentId: Long): Flow<List<Report>> {
        return reportDao.getReportsByAssignmentFlow(assignmentId).map { entities ->
            ReportMapper.toDomainList(entities)
        }
    }

    override suspend fun getReportsByAssignment(assignmentId: Long): List<Report> {
        return getReportsByAssignmentFlow(assignmentId).first()
    }

    override fun getAllReportsFlow(): Flow<List<Report>> {
        return reportDao.getAllReportsFlow().map { entities ->
            ReportMapper.toDomainList(entities)
        }
    }

    override suspend fun saveSimilarities(similarities: List<Similarity>) {
        val similarityEntities = similarities.map { SimilarityMapper.toEntity(it) }
        similarityDao.insertSimilarities(similarityEntities)
    }

    override suspend fun createSimilarity(similarity: Similarity): Long {
        val similarityEntity = SimilarityMapper.toEntity(similarity)
        return similarityDao.insertSimilarity(similarityEntity)
    }

    override fun getSimilaritiesByReportFlow(reportId: Long): Flow<List<Similarity>> {
        return similarityDao.getSimilaritiesByReportFlow(reportId).map { entities ->
            SimilarityMapper.toDomainList(entities)
        }
    }

    override suspend fun getSimilaritiesByReport(reportId: Long): List<Similarity> {
        return getSimilaritiesByReportFlow(reportId).first()
    }

    override fun getHighSimilaritiesFlow(reportId: Long, threshold: Float): Flow<List<Similarity>> {
        return similarityDao.getHighSimilaritiesFlow(reportId, threshold).map { entities ->
            SimilarityMapper.toDomainList(entities)
        }
    }

    override suspend fun getSimilarityById(similarityId: Long): Similarity? {
        val entity = similarityDao.getSimilarityById(similarityId) ?: return null
        return SimilarityMapper.toDomain(entity)
    }

    override suspend fun updateSimilarity(similarity: Similarity) {
        val entity = SimilarityMapper.toEntity(similarity)
        similarityDao.updateSimilarity(entity)
    }
}
