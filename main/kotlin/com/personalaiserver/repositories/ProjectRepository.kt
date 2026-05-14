package com.personalaiserver.repositories

import com.personalaiserver.database.Chats
import com.personalaiserver.database.Projects
import com.personalaiserver.models.Project
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.*

class ProjectRepository {

    fun findById(id: UUID): Project? = transaction {
        Projects.select { Projects.id eq id }.singleOrNull()?.toProject()
    }

    fun findByUserId(userId: UUID): List<Project> = transaction {
        Projects.select { Projects.userId eq userId }
            .orderBy(Projects.updatedAt, SortOrder.DESC)
            .map { it.toProject() }
    }

    fun create(userId: UUID, name: String, systemPrompt: String?, personalityStyle: String?): Project = transaction {
        val id = UUID.randomUUID()
        val now = LocalDateTime.now()
        Projects.insert { row ->
            row[Projects.id] = id
            row[Projects.userId] = userId
            row[Projects.name] = name
            row[Projects.systemPrompt] = systemPrompt
            row[Projects.personalityStyle] = personalityStyle
            row[Projects.createdAt] = now
            row[Projects.updatedAt] = now
        }
        Project(id, userId, name, systemPrompt, personalityStyle, now, now)
    }

    fun update(id: UUID, name: String?, systemPrompt: String?, personalityStyle: String?): Project? = transaction {
        val now = LocalDateTime.now()
        Projects.update({ Projects.id eq id }) { row ->
            if (name != null) row[Projects.name] = name
            if (systemPrompt != null) row[Projects.systemPrompt] = systemPrompt
            if (personalityStyle != null) row[Projects.personalityStyle] = personalityStyle
            row[Projects.updatedAt] = now
        }
        findById(id)
    }

    fun delete(id: UUID): Boolean = transaction {
        Projects.deleteWhere { Projects.id eq id } > 0
    }

    fun getChatCount(projectId: UUID): Int = transaction {
        Chats.select { Chats.projectId eq projectId }.count().toInt()
    }

    private fun ResultRow.toProject(): Project = Project(
        id = this[Projects.id],
        userId = this[Projects.userId],
        name = this[Projects.name],
        systemPrompt = this[Projects.systemPrompt],
        personalityStyle = this[Projects.personalityStyle],
        createdAt = this[Projects.createdAt],
        updatedAt = this[Projects.updatedAt]
    )
}
