package com.personalaiserver.auth

import com.personalaiserver.config.AppConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("SupabaseAuthService")

@Serializable
data class SupabaseUserResponse(
    val id: String,
    val email: String,
    @kotlinx.serialization.SerialName("user_metadata")
    val userMetadata: Map<String, String>? = null
)

class SupabaseAuthService(private val appConfig: AppConfig) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getUserFromToken(accessToken: String): SupabaseUserResponse? {
        return try {
            val client = HttpClient()
            val response = client.get("${appConfig.supabaseUrl}/auth/v1/user") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
                header("apikey", appConfig.supabaseKey)
            }
            client.close()

            if (response.status == HttpStatusCode.OK) {
                response.body<SupabaseUserResponse>()
            } else {
                log.warn("Supabase user lookup failed: ${response.status}")
                null
            }
        } catch (e: Exception) {
            log.error("Supabase auth request failed", e)
            null
        }
    }
}
