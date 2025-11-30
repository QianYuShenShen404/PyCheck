package com.example.codechecker.data.local.dao

import androidx.room.*
import com.example.codechecker.data.local.entity.SimilarityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SimilarityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSimilarity(similarity: SimilarityEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSimilarities(similarities: List<SimilarityEntity>)

    @Update
    suspend fun updateSimilarity(similarity: SimilarityEntity)

    @Delete
    suspend fun deleteSimilarity(similarity: SimilarityEntity)

    @Query("SELECT * FROM similarity_pairs WHERE report_id = :reportId ORDER BY similarity_score DESC")
    fun getSimilaritiesByReportFlow(reportId: Long): Flow<List<SimilarityEntity>>

    @Query("SELECT * FROM similarity_pairs WHERE report_id = :reportId ORDER BY similarity_score DESC")
    suspend fun getSimilaritiesByReport(reportId: Long): List<SimilarityEntity>

    @Query("SELECT * FROM similarity_pairs WHERE report_id = :reportId AND similarity_score >= :threshold ORDER BY similarity_score DESC")
    fun getHighSimilaritiesFlow(
        reportId: Long,
        threshold: Float = 60f
    ): Flow<List<SimilarityEntity>>
}
