package com.example.codechecker.di

import android.content.Context
import com.example.codechecker.data.preference.UserSessionManager
import com.example.codechecker.util.CryptoUtils
import com.example.codechecker.util.FileUtils
import com.example.codechecker.util.MD5Utils
import com.example.codechecker.util.TimeUtils
import com.example.codechecker.util.ValidationUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for utility dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object UtilityModule {


    /**
     * Provides CryptoUtils
     */
    @Provides
    @Singleton
    fun provideCryptoUtils(): CryptoUtils {
        return CryptoUtils()
    }

    /**
     * Provides FileUtils
     */
    @Provides
    @Singleton
    fun provideFileUtils(@ApplicationContext context: Context): FileUtils {
        return FileUtils(context)
    }

    /**
     * Provides MD5Utils
     */
    @Provides
    @Singleton
    fun provideMD5Utils(): MD5Utils {
        return MD5Utils()
    }

    /**
     * Provides TimeUtils
     */
    @Provides
    @Singleton
    fun provideTimeUtils(): TimeUtils {
        return TimeUtils()
    }

    /**
     * Provides ValidationUtils
     */
    @Provides
    @Singleton
    fun provideValidationUtils(): ValidationUtils {
        return ValidationUtils()
    }
}

/**
 * Entry point for accessing utility dependencies in Compose
 */
@dagger.hilt.EntryPoint
@InstallIn(SingletonComponent::class)
interface UtilityModuleEntryPoint {
    fun userSessionManager(): UserSessionManager
    fun cryptoUtils(): CryptoUtils
    fun fileUtils(): FileUtils
    fun md5Utils(): MD5Utils
    fun timeUtils(): TimeUtils
    fun validationUtils(): ValidationUtils
}

@dagger.hilt.EntryPoint
@InstallIn(SingletonComponent::class)
interface AppEntryPoint {
    fun databaseValidationUseCase(): com.example.codechecker.domain.usecase.DatabaseValidationUseCase
    fun ensureAdminExistsUseCase(): com.example.codechecker.domain.usecase.EnsureAdminExistsUseCase
}
