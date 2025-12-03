package com.example.codechecker.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarkAlertAsReadUseCase @Inject constructor() {
    suspend fun execute(alertId: String): Result<Unit> = withContext(Dispatchers.IO) {
        Result.success(Unit)
    }
}

