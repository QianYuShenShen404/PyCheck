package com.example.codechecker.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataCleanupUseCase @Inject constructor() {
    suspend fun execute(
        cleanupLogs: Boolean,
        cleanupOldUsers: Boolean,
        daysToKeep: Int
    ): Result<Int> = withContext(Dispatchers.IO) {
        Result.success(0)
    }
}

