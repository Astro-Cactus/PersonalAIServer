package com.personalaiserver

import com.personalaiserver.ai.AiService
import com.personalaiserver.ai.OpenRouterService
import com.personalaiserver.ai.PromptBuilder
import com.personalaiserver.auth.JwtService
import com.personalaiserver.auth.SupabaseAuthService
import com.personalaiserver.config.AppConfig
import com.personalaiserver.database.DatabaseFactory
import com.personalaiserver.plugins.configureRouting
import com.personalaiserver.plugins.configureSecurity
import com.personalaiserver.plugins.configureSerialization
import com.personalaiserver.plugins.configureStatusPages
import com.personalaiserver.repositories.ChatRepository
import com.personalaiserver.repositories.MessageRepository
import com.personalaiserver.repositories.ProjectRepository
import com.personalaiserver.repositories.UserRepository
import com.personalaiserver.repositories.UserSettingsRepository
import com.personalaiserver.services.ChatService
import com.personalaiserver.services.ProjectService
import com.personalaiserver.services.UserService
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = getPort(), module = Application::module).start(wait = true)
}

private fun getPort(): Int = System.getenv("PORT")?.toIntOrNull() ?: 8080

fun Application.module() {
    val appConfig = AppConfig()

    DatabaseFactory.init(appConfig)

    val jwtService = JwtService(appConfig)
    val supabaseAuthService = SupabaseAuthService(appConfig)

    val userRepository = UserRepository()
    val projectRepository = ProjectRepository()
    val chatRepository = ChatRepository()
    val messageRepository = MessageRepository()
    val userSettingsRepository = UserSettingsRepository()

    val userService = UserService(userRepository, userSettingsRepository)
    val projectService = ProjectService(projectRepository)
    val chatService = ChatService(chatRepository, messageRepository)

    val openRouterService = OpenRouterService(appConfig)
    val promptBuilder = PromptBuilder()
    val aiService = AiService(
        openRouterService = openRouterService,
        promptBuilder = promptBuilder,
        messageRepository = messageRepository,
        userService = userService,
        projectService = projectService,
        chatService = chatService
    )

    configureSerialization()
    configureStatusPages()
    configureSecurity(appConfig, jwtService)
    configureRouting(
        jwtService = jwtService,
        aiService = aiService,
        projectService = projectService,
        chatService = chatService,
        userService = userService,
        supabaseAuthService = supabaseAuthService
    )
}
