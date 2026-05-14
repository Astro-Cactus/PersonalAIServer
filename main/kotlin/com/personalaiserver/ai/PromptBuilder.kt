package com.personalaiserver.ai

class PromptBuilder {

    fun buildSystemPrompt(
        globalPrompt: String?,
        projectPrompt: String?,
        personalityStyle: String?,
        userName: String?,
        adaptiveMode: Boolean,
        userMessage: String
    ): String {
        val parts = mutableListOf<String>()

        parts.add("You are a helpful, intelligent AI assistant.")

        if (userName != null) {
            parts.add("The user's name is $userName.")
        }

        if (personalityStyle != null) {
            val styleInstructions = getPersonalityInstructions(personalityStyle)
            if (styleInstructions != null) {
                parts.add(styleInstructions)
            }
        }

        if (projectPrompt != null) {
            parts.add("Project context: $projectPrompt")
        }

        if (globalPrompt != null) {
            parts.add("Global instruction: $globalPrompt")
        }

        if (adaptiveMode) {
            val responseStyle = getAdaptiveResponseStyle(userMessage)
            parts.add(responseStyle)
        }

        return parts.joinToString("\n\n")
    }

    private fun getPersonalityInstructions(style: String): String? {
        return when (style.lowercase()) {
            "professional" -> "Respond in a formal, professional tone. Be precise and data-driven."
            "friendly" -> "Respond in a warm, friendly tone. Be approachable and conversational."
            "humorous" -> "Respond with a light-hearted, humorous tone. Use wit appropriately."
            "educational" -> "Respond in an instructive tone. Explain concepts thoroughly and provide examples."
            "concise" -> "Respond as briefly as possible while still being helpful. Get straight to the point."
            "empathetic" -> "Respond with empathy and emotional intelligence. Acknowledge feelings."
            else -> null
        }
    }

    private fun getAdaptiveResponseStyle(userMessage: String): String {
        return if (userMessage.length < 50) {
            "The user sent a brief message. Keep your response concise and direct."
        } else {
            "The user sent a detailed message. Provide a thorough, well-structured response."
        }
    }
}
