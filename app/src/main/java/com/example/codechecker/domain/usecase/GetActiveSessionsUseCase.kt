package com.example.codechecker.domain.usecase

import com.example.codechecker.ui.screens.admin.viewmodel.SecuritySession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetActiveSessionsUseCase @Inject constructor() {
    suspend fun execute(): List<SecuritySession> = withContext(Dispatchers.IO) {
        emptyList()
    }
}

