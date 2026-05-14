package com.personalaiserver.routes

import com.personalaiserver.auth.JwtService
import com.personalaiserver.dto.CreateProjectRequest
import com.personalaiserver.dto.UpdateProjectRequest
import com.personalaiserver.plugins.BadRequestException
import com.personalaiserver.plugins.UnauthorizedException
import com.personalaiserver.services.ProjectService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import java.util.*

fun Route.projectRoutes(jwtService: JwtService, projectService: ProjectService) {
    route("/projects") {
        authenticate("jwt") {
            get {
                val userId = jwtService.getUserIdFromPrincipal(call.principal())
                    ?: throw UnauthorizedException("Invalid token")
                val projects = projectService.listProjects(userId)
                call.respond(mapOf("projects" to projects))
            }

            post {
                val userId = jwtService.getUserIdFromPrincipal(call.principal())
                    ?: throw UnauthorizedException("Invalid token")
                val request = call.receive<CreateProjectRequest>()
                val project = projectService.createProject(
                    userId = userId,
                    name = request.name,
                    systemPrompt = request.systemPrompt,
                    personalityStyle = request.personalityStyle
                )
                call.respond(project)
            }

            get("/{id}") {
                val userId = jwtService.getUserIdFromPrincipal(call.principal())
                    ?: throw UnauthorizedException("Invalid token")
                val projectId = call.parameters["id"]?.let { UUID.fromString(it) }
                    ?: throw BadRequestException("Invalid project ID")
                val project = projectService.getProject(projectId, userId)
                call.respond(project)
            }

            put("/{id}") {
                val userId = jwtService.getUserIdFromPrincipal(call.principal())
                    ?: throw UnauthorizedException("Invalid token")
                val projectId = call.parameters["id"]?.let { UUID.fromString(it) }
                    ?: throw BadRequestException("Invalid project ID")
                val request = call.receive<UpdateProjectRequest>()
                val project = projectService.updateProject(projectId, userId, request)
                call.respond(project)
            }

            delete("/{id}") {
                val userId = jwtService.getUserIdFromPrincipal(call.principal())
                    ?: throw UnauthorizedException("Invalid token")
                val projectId = call.parameters["id"]?.let { UUID.fromString(it) }
                    ?: throw BadRequestException("Invalid project ID")
                projectService.deleteProject(projectId, userId)
                call.respond(mapOf("deleted" to true))
            }
        }
    }
}
