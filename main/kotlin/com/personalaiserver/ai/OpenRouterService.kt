package com.personalaiserver.ai

import com.personalaiserver.config.AppConfig
import com.personalaiserver.plugins.AiServiceException
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("OpenRouterService")

@Serializable
data class OpenRouterMessage(
    val role: String,
    val content: String
)

@Serializable
data class OpenRouterRequest(
    val model: String,
    val messages: List<OpenRouterMessage>,
    val temperature: Double = 0.7,
    val maxTokens: Int = 2048
)

@Serializable
data class OpenRouterChoice(
    val message: OpenRouterMessage,
    val finishReason: String? = null
)

@Serializable
data class OpenRouterResponse(
    val id: String,
    val choices: List<OpenRouterChoice>,
    val usage: OpenRouterUsage? = null,
    val error: OpenRouterError? = null
)

@Serializable
data class OpenRouterError(
    val message: String,
    val code: Int? = null
)

@Serializable
data class OpenRouterUsage(
    val promptTokens: Int = 0,
    val completionTokens: Int = 0,
    val totalTokens: Int = 0
)

class OpenRouterService(private val appConfig: AppConfig) {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 60000
            connectTimeoutMillis = 10000
            socketTimeoutMillis = 60000
        }
    }

    suspend fun sendChatCompletion(
        model: String,
        messages: List<Pair<String, String>>,
        temperature: Double = 0.7
    ): String {
        val openRouterMessages = messages.map { (role, content) ->
            OpenRouterMessage(role = role, content = content)
        }

        val request = OpenRouterRequest(
            model = model,
            messages = openRouterMessages,
            temperature = temperature
        )

        return try {
            val response: HttpResponse = client.post("${appConfig.openRouterBaseUrl}/chat/completions") {
                header(HttpHeaders.Authorization, "Bearer ${appConfig.openRouterApiKey}")
                header("HTTP-Referer", "https://personal-ai-server.com")
                header("X-Title", "Personal AI Server")
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status == HttpStatusCode.OK) {
                val body = response.body<OpenRouterResponse>()
                if (body.error != null) {
                    throw AiServiceException("OpenRouter error: ${body.error.message}")
                }
                body.choices.firstOrNull()?.message?.content
                    ?: throw AiServiceException("OpenRouter returned empty response")
            } else {
                val errorText = response.bodyAsText()
                log.error("OpenRouter API error: status=${response.status}, body=$errorText")
                throw AiServiceException("OpenRouter API returned status ${response.status}")
            }
        } catch (e: AiServiceException) {
            throw e
        } catch (e: Exception) {
            log.error("OpenRouter request failed", e)
            throw AiServiceException("Failed to communicate with AI service: ${e.message}")
        }
    }
}
