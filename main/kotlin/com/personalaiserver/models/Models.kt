package com.personalaiserver.models

import java.time.LocalDateTime
import java.util.*

data class User(
    val id: UUID,
    val email: String,
    val username: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class UserSettings(
    val id: UUID,
    val userId: UUID,
    val globalPrompt: String?,
    val adaptiveMode: Boolean,
    val defaultModel: String
)

data class Project(
    val id: UUID,
    val userId: UUID,
    val name: String,
    val systemPrompt: String?,
    val personalityStyle: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class Chat(
    val id: UUID,
    val projectId: UUID,
    val userId: UUID,
    val name: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class Message(
    val id: UUID,
    val chatId: UUID,
    val role: String,
    val content: String,
    val createdAt: LocalDateTime
)
