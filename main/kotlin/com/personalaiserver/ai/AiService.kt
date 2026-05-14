package com.personalaiserver.ai

import com.personalaiserver.dto.ChatSendResponse
import com.personalaiserver.plugins.BadRequestException
import com.personalaiserver.plugins.NotFoundException
import com.personalaiserver.repositories.MessageRepository
import com.personalaiserver.services.ChatService
import com.personalaiserver.services.ProjectService
import com.personalaiserver.services.UserService
import java.time.ZoneOffset
import java.util.*

class AiService(
    private val openRouterService: OpenRouterService,
    private val promptBuilder: PromptBuilder,
    private val messageRepository: MessageRepository,
    private val userService: UserService,
    private val projectService: ProjectService,
    private val chatService: ChatService
) {
    suspend fun sendMessage(
        userId: UUID,
        message: String,
        chatId: UUID?,
        projectId: UUID?
    ): ChatSendResponse {
        if (message.isBlank()) throw BadRequestException("Message cannot be empty")

        val chat = if (chatId != null) {
            chatService.findById(chatId) ?: throw NotFoundException("Chat not found")
        } else {
            throw BadRequestException("Chat ID is required")
        }

        val resolvedProjectId = projectId ?: chat.projectId
        val project = projectService.findById(resolvedProjectId)
            ?: throw NotFoundException("Project not found")

        val user = userService.getUser(userId)
            ?: throw NotFoundException("User not found")

        val userSettings = userService.getUserSettings(userId)

        val systemPrompt = promptBuilder.buildSystemPrompt(
            globalPrompt = userSettings.globalPrompt,
            projectPrompt = project.systemPrompt,
            personalityStyle = project.personalityStyle,
            userName = user.username,
            adaptiveMode = userSettings.adaptiveMode,
            userMessage = message
        )

        val history = chatService.getMessageHistory(chat.id, limit = 50)
        val model = userSettings.defaultModel

        val messages = buildList {
            add("system" to systemPrompt)
            history.forEach { msg ->
                add(msg.role to msg.content)
            }
            add("user" to message)
        }

        chatService.saveMessage(chat.id, "user", message)

        val aiResponse = openRouterService.sendChatCompletion(
            model = model,
            messages = messages
        )

        val savedAiMessage = chatService.saveMessage(chat.id, "assistant", aiResponse)

        return ChatSendResponse(
            messageId = savedAiMessage.id.toString(),
            content = aiResponse,
            role = "assistant",
            createdAt = savedAiMessage.createdAt.toEpochSecond(ZoneOffset.UTC) * 1000
        )
    }
}
