package com.personalaiserver.plugins

import com.personalaiserver.dto.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.slf4j.LoggerFactory

private val statusPageLogger = LoggerFactory.getLogger("StatusPages")

class BadRequestException(message: String) : RuntimeException(message)
class UnauthorizedException(message: String) : RuntimeException(message)
class NotFoundException(message: String) : RuntimeException(message)
class AiServiceException(message: String) : RuntimeException(message)

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<BadRequestException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(cause.message ?: "Bad request"))
        }
        exception<UnauthorizedException> { call, cause ->
            call.respond(HttpStatusCode.Unauthorized, ErrorResponse(cause.message ?: "Unauthorized"))
        }
        exception<NotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, ErrorResponse(cause.message ?: "Not found"))
        }
        exception<AiServiceException> { call, cause ->
            statusPageLogger.error("AI service error", cause)
            call.respond(HttpStatusCode.BadGateway, ErrorResponse(cause.message ?: "AI service unavailable"))
        }
        exception<Throwable> { call, cause ->
            statusPageLogger.error("Unhandled exception", cause)
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Internal server error"))
        }
    }
}
