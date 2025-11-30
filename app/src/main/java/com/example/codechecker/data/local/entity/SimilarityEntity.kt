package com.example.codechecker.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Similarity entity representing similarity between two submissions
 */
@Entity(tableName = "similarity_pairs")
data class SimilarityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "report_id")
    val reportId: Long,

    @ColumnInfo(name = "submission1_id")
    val submission1Id: Long,

    @ColumnInfo(name = "submission2_id")
    val submission2Id: Long,

    @ColumnInfo(name = "similarity_score")
    val similarityScore: Float, // Combined score (0-100)

    @ColumnInfo(name = "jaccard_score")
    val jaccardScore: Float, // Jaccard similarity score (0-100)

    @ColumnInfo(name = "lcs_score")
    val lcsScore: Float, // LCS similarity score (0-100)

    @ColumnInfo(name = "highlight_data")
    val highlightData: String, // JSON string with highlight information

    @ColumnInfo(name = "ai_analysis")
    val aiAnalysis: String?, // JSON string with AI analysis results

    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
