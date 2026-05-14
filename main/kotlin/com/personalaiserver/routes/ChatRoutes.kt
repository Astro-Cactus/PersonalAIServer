package com.personalaiserver.routes

import com.personalaiserver.ai.AiService
import com.personalaiserver.auth.JwtService
import com.personalaiserver.dto.ChatSendRequest
import com.personalaiserver.dto.CreateChatRequest
import com.personalaiserver.dto.UpdateChatRequest
import com.personalaiserver.plugins.BadRequestException
import com.personalaiserver.plugins.UnauthorizedException
import com.personalaiserver.services.ChatService
import com.personalaiserver.services.ProjectService
import com.personalaiserver.services.UserService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import java.util.*

fun Route.chatRoutes(
    jwtService: JwtService,
    aiService: AiService,
    projectService: ProjectService,
    chatService: ChatService,
    userService: UserService
) {
    route("/chats") {
        authenticate("jwt") {
            get {
                val userId = jwtService.getUserIdFromPrincipal(call.principal())
                    ?: throw UnauthorizedException("Invalid token")
                val projectId = call.request.queryParameters["projectId"]?.let { UUID.fromString(it) }

                val chats = if (projectId != null) {
                    chatService.findByProjectId(projectId, userId)
                } else {
                    chatService.findByUserId(userId)
                }
                call.respond(mapOf("chats" to chats))
            }

            post {
                val userId = jwtService.getUserIdFromPrincipal(call.principal())
                    ?: throw UnauthorizedException("Invalid token")
                val request = call.receive<CreateChatRequest>()
                val projectId = UUID.fromString(request.projectId)
                val chat = chatService.createChat(userId, projectId, request.name)
                call.respond(chat)
            }

            get("/{id}") {
                val userId = jwtService.getUserIdFromPrincipal(call.principal())
                    ?: throw UnauthorizedException("Invalid token")
                val chatId = call.parameters["id"]?.let { UUID.fromString(it) }
                    ?: throw BadRequestException("Invalid chat ID")
                val history = chatService.getChatHistory(chatId, userId)
                call.respond(history)
            }

            put("/{id}") {
                val userId = jwtService.getUserIdFromPrincipal(call.principal())
                    ?: throw UnauthorizedException("Invalid token")
                val chatId = call.parameters["id"]?.let { UUID.fromString(it) }
                    ?: throw BadRequestException("Invalid chat ID")
                val request = call.receive<UpdateChatRequest>()
                val chat = chatService.renameChat(chatId, userId, request.name)
                call.respond(chat)
            }

            delete("/{id}") {
                val userId = jwtService.getUserIdFromPrincipal(call.principal())
                    ?: throw UnauthorizedException("Invalid token")
                val chatId = call.parameters["id"]?.let { UUID.fromString(it) }
                    ?: throw BadRequestException("Invalid chat ID")
                chatService.deleteChat(chatId, userId)
                call.respond(mapOf("deleted" to true))
            }
        }
    }

    route("/chat") {
        authenticate("jwt") {
            post("/send") {
                val userId = jwtService.getUserIdFromPrincipal(call.principal())
                    ?: throw UnauthorizedException("Invalid token")
                val request = call.receive<ChatSendRequest>()

                val chatId = UUID.fromString(request.chatId)
                val projectId = UUID.fromString(request.projectId)

                val response = aiService.sendMessage(
                    userId = userId,
                    message = request.message,
                    chatId = chatId,
                    projectId = projectId
                )
                call.respond(response)
            }
        }
    }
}
