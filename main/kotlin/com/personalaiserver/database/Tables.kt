package com.personalaiserver.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Users : Table("users") {
    val id = uuid("id")
    val email = varchar("email", 255)
    val username = varchar("username", 100).nullable()
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
}

object UserSettings : Table("user_settings") {
    val id = uuid("id")
    val userId = uuid("user_id").references(Users.id)
    val globalPrompt = text("global_prompt").nullable()
    val adaptiveMode = bool("adaptive_mode").default(true)
    val defaultModel = varchar("default_model", 100).default("google/gemma-4-26b-a4b-it:free")

    override val primaryKey = PrimaryKey(id)
}

object Projects : Table("projects") {
    val id = uuid("id")
    val userId = uuid("user_id").references(Users.id)
    val name = varchar("name", 255)
    val systemPrompt = text("system_prompt").nullable()
    val personalityStyle = varchar("personality_style", 100).nullable()
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
}

object Chats : Table("chats") {
    val id = uuid("id")
    val projectId = uuid("project_id").references(Projects.id)
    val userId = uuid("user_id").references(Users.id)
    val name = varchar("name", 255)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
}

object Messages : Table("messages") {
    val id = uuid("id")
    val chatId = uuid("chat_id").references(Chats.id)
    val role = varchar("role", 20)
    val content = text("content")
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}
