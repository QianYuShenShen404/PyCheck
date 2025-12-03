package com.example.codechecker.di

import com.example.codechecker.domain.repository.AdminSettingsRepository
import com.example.codechecker.domain.repository.AIRepository
import com.example.codechecker.domain.repository.AssignmentRepository
import com.example.codechecker.data.repository.AdminSettingsRepositoryImpl
import com.example.codechecker.data.repository.AIRepositoryImpl
import com.example.codechecker.data.repository.AssignmentRepositoryImpl
import com.example.codechecker.domain.repository.ReportRepository
import com.example.codechecker.data.repository.ReportRepositoryImpl
import com.example.codechecker.domain.repository.SubmissionRepository
import com.example.codechecker.data.repository.SubmissionRepositoryImpl
import com.example.codechecker.domain.repository.UserRepository
import com.example.codechecker.data.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for repository dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Binds UserRepository implementation
     */
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    /**
     * Binds AssignmentRepository implementation
     */
    @Binds
    @Singleton
    abstract fun bindAssignmentRepository(
        assignmentRepositoryImpl: AssignmentRepositoryImpl
    ): AssignmentRepository

    /**
     * Binds SubmissionRepository implementation
     */
    @Binds
    @Singleton
    abstract fun bindSubmissionRepository(
        submissionRepositoryImpl: SubmissionRepositoryImpl
    ): SubmissionRepository

    /**
     * Binds ReportRepository implementation
     */
    @Binds
    @Singleton
    abstract fun bindReportRepository(
        reportRepositoryImpl: ReportRepositoryImpl
    ): ReportRepository

    /**
     * Binds AdminSettingsRepository implementation
     */
    @Binds
    @Singleton
    abstract fun bindAdminSettingsRepository(
        adminSettingsRepositoryImpl: AdminSettingsRepositoryImpl
    ): AdminSettingsRepository

    @Binds
    @Singleton
    abstract fun bindAIRepository(
        aiRepositoryImpl: AIRepositoryImpl
    ): AIRepository
}
