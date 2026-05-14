package com.personalaiserver.repositories

import com.personalaiserver.database.UserSettings
import com.personalaiserver.models.UserSettings as UserSettingsModel
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class UserSettingsRepository {

    fun findByUserId(userId: UUID): UserSettingsModel? = transaction {
        UserSettings.select { UserSettings.userId eq userId }.singleOrNull()?.toModel()
    }

    fun create(userId: UUID): UserSettingsModel = transaction {
        val id = UUID.randomUUID()
        UserSettings.insert { row ->
            row[UserSettings.id] = id
            row[UserSettings.userId] = userId
            row[UserSettings.globalPrompt] = null
            row[UserSettings.adaptiveMode] = true
            row[UserSettings.defaultModel] = "google/gemma-4-26b-a4b-it:free"
        }
        UserSettingsModel(
            id = id,
            userId = userId,
            globalPrompt = null,
            adaptiveMode = true,
            defaultModel = "google/gemma-4-26b-a4b-it:free"
        )
    }

    fun getOrCreate(userId: UUID): UserSettingsModel {
        return findByUserId(userId) ?: create(userId)
    }

    fun updateGlobalPrompt(userId: UUID, prompt: String?): UserSettingsModel? = transaction {
        UserSettings.update({ UserSettings.userId eq userId }) { row ->
            row[UserSettings.globalPrompt] = prompt
        }
        findByUserId(userId)
    }

    fun updateAdaptiveMode(userId: UUID, enabled: Boolean): UserSettingsModel? = transaction {
        UserSettings.update({ UserSettings.userId eq userId }) { row ->
            row[UserSettings.adaptiveMode] = enabled
        }
        findByUserId(userId)
    }

    fun updateDefaultModel(userId: UUID, model: String): UserSettingsModel? = transaction {
        UserSettings.update({ UserSettings.userId eq userId }) { row ->
            row[UserSettings.defaultModel] = model
        }
        findByUserId(userId)
    }

    private fun ResultRow.toModel(): UserSettingsModel = UserSettingsModel(
        id = this[UserSettings.id],
        userId = this[UserSettings.userId],
        globalPrompt = this[UserSettings.globalPrompt],
        adaptiveMode = this[UserSettings.adaptiveMode],
        defaultModel = this[UserSettings.defaultModel]
    )
}
