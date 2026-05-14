package com.personalaiserver.repositories

import com.personalaiserver.database.Messages
import com.personalaiserver.models.Message
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.*

class MessageRepository {

    fun findByChatId(chatId: UUID, limit: Int = 100): List<Message> = transaction {
        Messages.select { Messages.chatId eq chatId }
            .orderBy(Messages.createdAt, SortOrder.ASC)
            .limit(limit)
            .map { it.toMessage() }
    }

    fun create(chatId: UUID, role: String, content: String): Message = transaction {
        val id = UUID.randomUUID()
        val now = LocalDateTime.now()
        Messages.insert { row ->
            row[Messages.id] = id
            row[Messages.chatId] = chatId
            row[Messages.role] = role
            row[Messages.content] = content
            row[Messages.createdAt] = now
        }
        Message(id, chatId, role, content, now)
    }

    fun deleteByChatId(chatId: UUID) = transaction {
        Messages.deleteWhere { Messages.chatId eq chatId }
    }

    private fun ResultRow.toMessage(): Message = Message(
        id = this[Messages.id],
        chatId = this[Messages.chatId],
        role = this[Messages.role],
        content = this[Messages.content],
        createdAt = this[Messages.createdAt]
    )
}
