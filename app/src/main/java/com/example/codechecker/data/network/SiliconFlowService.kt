package com.example.codechecker.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class AIChatMessage(val role: String, val content: String)
data class AIChatRequest(val model: String, val messages: List<AIChatMessage>, val stream: Boolean = false)
data class AIChoiceMessage(val role: String?, val content: String?, val reasoning_content: String?)
data class AIChoice(val message: AIChoiceMessage?, val finish_reason: String?)
data class AIChatResponse(val id: String?, val choices: List<AIChoice>?)

interface SiliconFlowService {
    @POST("chat/completions")
    suspend fun chat(@Body body: AIChatRequest): Response<AIChatResponse>
}

