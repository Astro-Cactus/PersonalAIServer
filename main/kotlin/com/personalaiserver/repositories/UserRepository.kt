package com.personalaiserver.repositories

import com.personalaiserver.database.Users
import com.personalaiserver.models.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.*

class UserRepository {

    fun findById(id: UUID): User? = transaction {
        Users.select { Users.id eq id }.singleOrNull()?.toUser()
    }

    fun findByEmail(email: String): User? = transaction {
        Users.select { Users.email eq email }.singleOrNull()?.toUser()
    }

    fun create(id: UUID, email: String, username: String?): User = transaction {
        val now = LocalDateTime.now()
        Users.insert { row ->
            row[Users.id] = id
            row[Users.email] = email
            row[Users.username] = username
            row[Users.createdAt] = now
            row[Users.updatedAt] = now
        }
        User(id, email, username, now, now)
    }

    fun updateUsername(id: UUID, username: String): User? = transaction {
        val now = LocalDateTime.now()
        Users.update({ Users.id eq id }) { row ->
            row[Users.username] = username
            row[Users.updatedAt] = now
        }
        findById(id)
    }

    private fun ResultRow.toUser(): User = User(
        id = this[Users.id],
        email = this[Users.email],
        username = this[Users.username],
        createdAt = this[Users.createdAt],
        updatedAt = this[Users.updatedAt]
    )
}
