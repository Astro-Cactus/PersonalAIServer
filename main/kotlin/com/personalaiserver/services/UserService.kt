package com.personalaiserver.services

import com.personalaiserver.models.User
import com.personalaiserver.models.UserSettings
import com.personalaiserver.repositories.UserRepository
import com.personalaiserver.repositories.UserSettingsRepository
import java.util.*

class UserService(
    private val userRepository: UserRepository,
    private val userSettingsRepository: UserSettingsRepository
) {
    fun getOrCreateUser(id: UUID, email: String, username: String?): User {
        return userRepository.findById(id) ?: userRepository.create(id, email, username)
    }

    fun getUser(id: UUID): User? = userRepository.findById(id)

    fun getUserSettings(userId: UUID): UserSettings = userSettingsRepository.getOrCreate(userId)
}
