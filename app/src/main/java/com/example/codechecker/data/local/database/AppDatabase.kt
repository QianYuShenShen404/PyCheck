package com.example.codechecker.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import com.example.codechecker.data.local.dao.AssignmentDao
import com.example.codechecker.data.local.dao.ReportDao
import com.example.codechecker.data.local.dao.SubmissionDao
import com.example.codechecker.data.local.dao.UserDao
import com.example.codechecker.data.local.dao.SimilarityDao
import com.example.codechecker.data.local.entity.AssignmentEntity
import com.example.codechecker.data.local.entity.ReportEntity
import com.example.codechecker.data.local.entity.SubmissionEntity
import com.example.codechecker.data.local.entity.SimilarityEntity
import com.example.codechecker.data.local.entity.UserEntity

/**
 * Room database for CodeChecker
 */
@Database(
    entities = [
        UserEntity::class,
        AssignmentEntity::class,
        SubmissionEntity::class,
        ReportEntity::class,
        SimilarityEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters()
abstract class AppDatabase : RoomDatabase() {

    /**
     * Get User DAO
     */
    abstract fun userDao(): UserDao

    /**
     * Get Assignment DAO
     */
    abstract fun assignmentDao(): AssignmentDao

    /**
     * Get Submission DAO
     */
    abstract fun submissionDao(): SubmissionDao

    /**
     * Get Report DAO
     */
    abstract fun reportDao(): ReportDao

    /**
     * Get Similarity DAO
     */
    abstract fun similarityDao(): SimilarityDao
}
