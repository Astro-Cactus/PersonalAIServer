package com.personalaiserver.plugins

import com.personalaiserver.ai.AiService
import com.personalaiserver.auth.JwtService
import com.personalaiserver.auth.SupabaseAuthService
import com.personalaiserver.routes.chatRoutes
import com.personalaiserver.routes.healthRoutes
import com.personalaiserver.routes.projectRoutes
import com.personalaiserver.services.ChatService
import com.personalaiserver.services.ProjectService
import com.personalaiserver.services.UserService
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    jwtService: JwtService,
    aiService: AiService,
    projectService: ProjectService,
    chatService: ChatService,
    userService: UserService,
    supabaseAuthService: SupabaseAuthService
) {
    routing {
        healthRoutes()
        projectRoutes(jwtService, projectService)
        chatRoutes(jwtService, aiService, projectService, chatService, userService)
    }
}
