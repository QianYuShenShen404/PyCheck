package com.example.codechecker.data.repository

import com.example.codechecker.domain.model.AIAnalysisResult
import com.example.codechecker.domain.repository.AIRepository
import com.example.codechecker.domain.repository.AdminSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import com.example.codechecker.data.network.SiliconFlowService
import com.example.codechecker.data.network.AIChatMessage
import com.example.codechecker.data.network.AIChatRequest

@Singleton
class AIRepositoryImpl @Inject constructor(
    private val adminSettingsRepository: AdminSettingsRepository
) : AIRepository {

    override suspend fun analyze(code1: String, code2: String, similarity: Double): AIAnalysisResult = withContext(Dispatchers.IO) {
        val settings = adminSettingsRepository.getAdminSettings()
        if (settings.aiApiKey.isBlank()) return@withContext AIAnalysisResult.Error("未配置AI API密钥")

        val client = OkHttpClient.Builder()
            .connectTimeout(settings.aiConnectTimeoutSec.toLong(), TimeUnit.SECONDS)
            .readTimeout(settings.aiReadTimeoutSec.toLong(), TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val req = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${settings.aiApiKey}")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(req)
            }
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(settings.aiBaseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()

        val service = retrofit.create(SiliconFlowService::class.java)
        val prompt = buildPrompt(code1, code2, similarity)
        val request = AIChatRequest(
            model = settings.aiModel,
            messages = listOf(AIChatMessage(role = "user", content = prompt)),
            stream = false
        )

        retry(settings.aiRetryTimes.coerceAtLeast(1), 1000L) {
            val resp = service.chat(request)
            if (!resp.isSuccessful) throw HttpException(resp)
            val body = resp.body() ?: return@retry AIAnalysisResult.Error("空响应")
            parse(body)
        }
    }

    private fun buildPrompt(code1: String, code2: String, similarity: Double): String =
        """
        请分析两段Python代码的相似性并以JSON给出结论，字段：reason, isCommonCode, plagiarismRisk(low/medium/high), analysis。
        相似度：${"%.1f".format(similarity)}%
        代码1：
        ```python
        ${code1.take(4000)}
        ```
        代码2：
        ```python
        ${code2.take(4000)}
        ```
        """.trimIndent()

    private fun parse(resp: com.example.codechecker.data.network.AIChatResponse): AIAnalysisResult {
        val content = resp.choices?.firstOrNull()?.message?.content ?: return AIAnalysisResult.Error("无内容")
        return try {
            val j = org.json.JSONObject(content)
            AIAnalysisResult.Success(
                reason = j.optString("reason"),
                isCommonCode = j.optBoolean("isCommonCode", false),
                plagiarismRisk = j.optString("plagiarismRisk", "low"),
                analysis = j.optString("analysis")
            )
        } catch (_: Exception) {
            AIAnalysisResult.Error("JSON格式无效")
        }
    }

    private suspend fun <T> retry(times: Int, initialDelayMs: Long, block: suspend () -> T): T {
        var delayMs = initialDelayMs
        var last: Throwable? = null
        repeat(times) {
            try { return block() } catch (e: Throwable) {
                last = e
                if (e is SocketTimeoutException) throw e
                if (e is HttpException) {
                    val c = e.code()
                    if (c !in listOf(429) && c !in 500..599) throw e
                }
                delay(delayMs)
                delayMs *= 2
            }
        }
        throw last ?: RuntimeException("未知错误")
    }
}
