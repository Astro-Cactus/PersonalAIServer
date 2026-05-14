package com.personalaiserver.auth

import com.personalaiserver.config.AppConfig
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.*

class JwtService(private val appConfig: AppConfig) {

    fun getUserIdFromPrincipal(principal: Principal?): UUID? {
        val jwtPrincipal = principal as? JWTPrincipal ?: return null
        val subject = jwtPrincipal.payload.subject ?: return null
        return try {
            UUID.fromString(subject)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    fun getUserIdFromToken(token: String): UUID? {
        return try {
            val decoded = com.auth0.jwt.JWT.decode(token)
            val subject = decoded.subject ?: return null
            UUID.fromString(subject)
        } catch (e: Exception) {
            null
        }
    }
}
