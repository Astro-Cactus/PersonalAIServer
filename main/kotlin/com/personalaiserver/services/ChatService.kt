package com.personalaiserver.services

import com.personalaiserver.dto.*
import com.personalaiserver.models.Chat
import com.personalaiserver.models.Message
import com.personalaiserver.plugins.BadRequestException
import com.personalaiserver.plugins.NotFoundException
import com.personalaiserver.repositories.ChatRepository
import com.personalaiserver.repositories.MessageRepository
import java.time.ZoneOffset
import java.util.*

class ChatService(
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository
) {
    fun createChat(userId: UUID, projectId: UUID, name: String): ChatResponse {
        if (name.isBlank()) throw BadRequestException("Chat name is required")

        val chat = chatRepository.create(projectId, userId, name.trim())
        return chat.toResponse(0)
    }

    fun getChatHistory(chatId: UUID, userId: UUID): ChatHistoryResponse {
        val chat = chatRepository.findById(chatId)
            ?: throw NotFoundException("Chat not found")

        if (chat.userId != userId) throw NotFoundException("Chat not found")

        val messages = messageRepository.findByChatId(chatId)
        return ChatHistoryResponse(
            chatId = chat.id.toString(),
            name = chat.name,
            messages = messages.map { it.toResponse(chatId) }
        )
    }

    fun renameChat(chatId: UUID, userId: UUID, name: String): ChatResponse {
        if (name.isBlank()) throw BadRequestException("Chat name is required")

        val chat = chatRepository.findById(chatId)
            ?: throw NotFoundException("Chat not found")

        if (chat.userId != userId) throw NotFoundException("Chat not found")

        val updated = chatRepository.update(chatId, name.trim())
            ?: throw NotFoundException("Chat not found")

        val messageCount = chatRepository.getMessageCount(chatId)
        return updated.toResponse(messageCount)
    }

    fun deleteChat(chatId: UUID, userId: UUID) {
        val chat = chatRepository.findById(chatId)
            ?: throw NotFoundException("Chat not found")

        if (chat.userId != userId) throw NotFoundException("Chat not found")

        chatRepository.delete(chatId)
    }

    fun findByProjectId(projectId: UUID, userId: UUID): List<ChatResponse> {
        val projectChats = chatRepository.findByProjectId(projectId)
            .filter { it.userId == userId }
        return projectChats.map { chat ->
            val count = chatRepository.getMessageCount(chat.id)
            chat.toResponse(count)
        }
    }

    fun findByUserId(userId: UUID): List<ChatResponse> {
        return chatRepository.findByUserId(userId).map { chat ->
            val count = chatRepository.getMessageCount(chat.id)
            chat.toResponse(count)
        }
    }

    fun findById(chatId: UUID): Chat? = chatRepository.findById(chatId)

    fun getMessageHistory(chatId: UUID, limit: Int = 50): List<Message> =
        messageRepository.findByChatId(chatId, limit)

    fun saveMessage(chatId: UUID, role: String, content: String): Message =
        messageRepository.create(chatId, role, content)

    private fun Chat.toResponse(messageCount: Int) = ChatResponse(
        id = id.toString(),
        projectId = projectId.toString(),
        name = name,
        createdAt = createdAt.toEpochSecond(ZoneOffset.UTC) * 1000,
        updatedAt = updatedAt.toEpochSecond(ZoneOffset.UTC) * 1000,
        messageCount = messageCount
    )

    private fun Message.toResponse(chatId: UUID) = MessageResponse(
        id = id.toString(),
        chatId = chatId.toString(),
        role = role,
        content = content,
        createdAt = this.createdAt.toEpochSecond(ZoneOffset.UTC) * 1000
    )
}
