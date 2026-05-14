package com.personalaiserver.dto

import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(
    val status: String
)

@Serializable
data class ErrorResponse(
    val error: String
)

@Serializable
data class ChatSendResponse(
    val messageId: String,
    val content: String,
    val role: String = "assistant",
    val createdAt: Long
)

@Serializable
data class ProjectResponse(
    val id: String,
    val name: String,
    val systemPrompt: String? = null,
    val personalityStyle: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val chatCount: Int = 0
)

@Serializable
data class ProjectListResponse(
    val projects: List<ProjectResponse>
)

@Serializable
data class ChatResponse(
    val id: String,
    val projectId: String,
    val name: String,
    val createdAt: Long,
    val updatedAt: Long,
    val messageCount: Int = 0
)

@Serializable
data class ChatListResponse(
    val chats: List<ChatResponse>
)

@Serializable
data class MessageResponse(
    val id: String,
    val chatId: String,
    val role: String,
    val content: String,
    val createdAt: Long
)

@Serializable
data class ChatHistoryResponse(
    val chatId: String,
    val name: String,
    val messages: List<MessageResponse>
)

@Serializable
data class UserResponse(
    val id: String,
    val email: String,
    val username: String? = null
)
