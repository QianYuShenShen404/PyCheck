package com.example.codechecker.domain.usecase

import com.example.codechecker.ui.screens.admin.viewmodel.SecurityAlert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSecurityAlertsUseCase @Inject constructor() {
    suspend fun execute(): List<SecurityAlert> = withContext(Dispatchers.IO) {
        emptyList()
    }
}

