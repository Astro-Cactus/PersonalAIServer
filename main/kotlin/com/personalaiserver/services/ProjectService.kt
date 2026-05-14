package com.personalaiserver.services

import com.personalaiserver.dto.ProjectResponse
import com.personalaiserver.dto.UpdateProjectRequest
import com.personalaiserver.models.Project
import com.personalaiserver.plugins.BadRequestException
import com.personalaiserver.plugins.NotFoundException
import com.personalaiserver.repositories.ProjectRepository
import java.time.ZoneOffset
import java.util.*

class ProjectService(
    private val projectRepository: ProjectRepository
) {
    fun createProject(userId: UUID, name: String, systemPrompt: String?, personalityStyle: String?): ProjectResponse {
        if (name.isBlank()) throw BadRequestException("Project name is required")

        val project = projectRepository.create(userId, name.trim(), systemPrompt, personalityStyle)
        return project.toResponse(0)
    }

    fun getProject(projectId: UUID, userId: UUID): ProjectResponse {
        val project = projectRepository.findById(projectId)
            ?: throw NotFoundException("Project not found")

        if (project.userId != userId) throw NotFoundException("Project not found")

        val chatCount = projectRepository.getChatCount(projectId)
        return project.toResponse(chatCount)
    }

    fun listProjects(userId: UUID): List<ProjectResponse> {
        return projectRepository.findByUserId(userId).map { project ->
            val chatCount = projectRepository.getChatCount(project.id)
            project.toResponse(chatCount)
        }
    }

    fun updateProject(projectId: UUID, userId: UUID, request: UpdateProjectRequest): ProjectResponse {
        val project = projectRepository.findById(projectId)
            ?: throw NotFoundException("Project not found")

        if (project.userId != userId) throw NotFoundException("Project not found")

        val updated = projectRepository.update(projectId, request.name?.trim(), request.systemPrompt, request.personalityStyle)
            ?: throw NotFoundException("Project not found")

        val chatCount = projectRepository.getChatCount(projectId)
        return updated.toResponse(chatCount)
    }

    fun deleteProject(projectId: UUID, userId: UUID) {
        val project = projectRepository.findById(projectId)
            ?: throw NotFoundException("Project not found")

        if (project.userId != userId) throw NotFoundException("Project not found")

        projectRepository.delete(projectId)
    }

    fun findById(projectId: UUID): Project? = projectRepository.findById(projectId)

    private fun Project.toResponse(chatCount: Int) = ProjectResponse(
        id = id.toString(),
        name = name,
        systemPrompt = systemPrompt,
        personalityStyle = personalityStyle,
        createdAt = createdAt.toEpochSecond(ZoneOffset.UTC) * 1000,
        updatedAt = updatedAt.toEpochSecond(ZoneOffset.UTC) * 1000,
        chatCount = chatCount
    )
}
