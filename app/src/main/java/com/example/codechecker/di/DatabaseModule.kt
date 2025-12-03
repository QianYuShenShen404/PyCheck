package com.example.codechecker.di

import android.content.Context
import androidx.room.Room
import com.example.codechecker.data.local.database.AppDatabase
import com.example.codechecker.data.local.database.DatabaseCallback
import com.example.codechecker.data.local.database.MIGRATION_1_2
import com.example.codechecker.data.local.database.MIGRATION_3_4
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for database dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides the Room database instance
     */
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        databaseCallback: DatabaseCallback
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "codechecker_database"
        )
        .addCallback(databaseCallback)
        .addMigrations(MIGRATION_1_2, MIGRATION_3_4)
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): com.example.codechecker.data.local.dao.UserDao {
        return database.userDao()
    }

    @Provides
    fun provideAssignmentDao(database: AppDatabase): com.example.codechecker.data.local.dao.AssignmentDao {
        return database.assignmentDao()
    }

    @Provides
    fun provideSubmissionDao(database: AppDatabase): com.example.codechecker.data.local.dao.SubmissionDao {
        return database.submissionDao()
    }

    @Provides
    fun provideReportDao(database: AppDatabase): com.example.codechecker.data.local.dao.ReportDao {
        return database.reportDao()
    }

    @Provides
    fun provideSimilarityDao(database: AppDatabase): com.example.codechecker.data.local.dao.SimilarityDao {
        return database.similarityDao()
    }

    @Provides
    fun provideAdminAuditLogDao(database: AppDatabase): com.example.codechecker.data.local.dao.AdminAuditLogDao {
        return database.adminAuditLogDao()
    }

    @Provides
    fun provideAdminSettingDao(database: AppDatabase): com.example.codechecker.data.local.dao.AdminSettingDao {
        return database.adminSettingDao()
    }
}
