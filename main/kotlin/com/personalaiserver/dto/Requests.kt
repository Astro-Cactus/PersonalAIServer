package com.personalaiserver.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatSendRequest(
    val message: String,
    val chatId: String,
    val projectId: String
)

@Serializable
data class CreateProjectRequest(
    val name: String,
    val systemPrompt: String? = null,
    val personalityStyle: String? = null
)

@Serializable
data class UpdateProjectRequest(
    val name: String? = null,
    val systemPrompt: String? = null,
    val personalityStyle: String? = null
)

@Serializable
data class CreateChatRequest(
    val projectId: String,
    val name: String
)

@Serializable
data class UpdateChatRequest(
    val name: String
)

@Serializable
data class SyncUserRequest(
    val email: String,
    val username: String? = null
)
