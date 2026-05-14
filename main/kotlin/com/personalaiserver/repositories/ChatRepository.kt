package com.personalaiserver.repositories

import com.personalaiserver.database.Chats
import com.personalaiserver.database.Messages
import com.personalaiserver.models.Chat
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.*

class ChatRepository {

    fun findById(id: UUID): Chat? = transaction {
        Chats.select { Chats.id eq id }.singleOrNull()?.toChat()
    }

    fun findByProjectId(projectId: UUID): List<Chat> = transaction {
        Chats.select { Chats.projectId eq projectId }
            .orderBy(Chats.updatedAt, SortOrder.DESC)
            .map { it.toChat() }
    }

    fun findByUserId(userId: UUID): List<Chat> = transaction {
        Chats.select { Chats.userId eq userId }
            .orderBy(Chats.updatedAt, SortOrder.DESC)
            .map { it.toChat() }
    }

    fun create(projectId: UUID, userId: UUID, name: String): Chat = transaction {
        val id = UUID.randomUUID()
        val now = LocalDateTime.now()
        Chats.insert { row ->
            row[Chats.id] = id
            row[Chats.projectId] = projectId
            row[Chats.userId] = userId
            row[Chats.name] = name
            row[Chats.createdAt] = now
            row[Chats.updatedAt] = now
        }
        Chat(id, projectId, userId, name, now, now)
    }

    fun update(id: UUID, name: String): Chat? = transaction {
        val now = LocalDateTime.now()
        Chats.update({ Chats.id eq id }) { row ->
            row[Chats.name] = name
            row[Chats.updatedAt] = now
        }
        findById(id)
    }

    fun delete(id: UUID): Boolean = transaction {
        Messages.deleteWhere { Messages.chatId eq id }
        Chats.deleteWhere { Chats.id eq id } > 0
    }

    fun getMessageCount(chatId: UUID): Int = transaction {
        Messages.select { Messages.chatId eq chatId }.count().toInt()
    }

    private fun ResultRow.toChat(): Chat = Chat(
        id = this[Chats.id],
        projectId = this[Chats.projectId],
        userId = this[Chats.userId],
        name = this[Chats.name],
        createdAt = this[Chats.createdAt],
        updatedAt = this[Chats.updatedAt]
    )
}
