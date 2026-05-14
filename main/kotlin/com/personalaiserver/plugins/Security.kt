package com.personalaiserver.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.personalaiserver.auth.JwtService
import com.personalaiserver.config.AppConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.plugins.callloging.*
import org.slf4j.event.*

fun Application.configureSecurity(appConfig: AppConfig, jwtService: JwtService) {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
    }

    install(CallLogging) {
        level = Level.INFO
    }

    install(Authentication) {
        jwt {
            realm = "personal-ai-server"
            verifier(
                JWT
                    .require(Algorithm.HMAC256(appConfig.jwtSecret))
                    .withIssuer(appConfig.jwtIssuer)
                    .withAudience("authenticated")
                    .acceptLeeway(5)
                    .build()
            )
            validate { credential ->
                val userId = credential.payload.subject
                if (userId != null) {
                    JWTPrincipal(credential.payload)
                } else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid or expired token"))
            }
        }
    }
}
