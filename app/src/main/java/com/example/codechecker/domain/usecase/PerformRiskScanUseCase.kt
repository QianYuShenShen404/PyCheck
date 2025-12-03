package com.example.codechecker.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PerformRiskScanUseCase @Inject constructor() {
    suspend fun execute(): Result<Unit> = withContext(Dispatchers.IO) {
        Result.success(Unit)
    }
}

