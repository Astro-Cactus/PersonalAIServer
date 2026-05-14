package com.personalaiserver.config

class AppConfig {
    val openRouterApiKey: String by lazy {
        requireNotNull(System.getenv("OPENROUTER_API_KEY")) {
            "OPENROUTER_API_KEY environment variable is required"
        }
    }

    val databaseUrl: String by lazy {
        System.getenv("DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/personal_ai_server"
    }

    val databaseUser: String by lazy {
        System.getenv("DATABASE_USER") ?: "postgres"
    }

    val databasePassword: String by lazy {
        System.getenv("DATABASE_PASSWORD") ?: "postgres"
    }

    val jwtSecret: String by lazy {
        requireNotNull(System.getenv("JWT_SECRET")) {
            "JWT_SECRET environment variable is required"
        }
    }

    val supabaseUrl: String by lazy {
        requireNotNull(System.getenv("SUPABASE_URL")) {
            "SUPABASE_URL environment variable is required"
        }
    }

    val supabaseKey: String by lazy {
        requireNotNull(System.getenv("SUPABASE_KEY")) {
            "SUPABASE_KEY environment variable is required"
        }
    }

    val jwtIssuer: String get() = "$supabaseUrl/auth/v1"

    val openRouterBaseUrl: String get() = "https://openrouter.ai/api/v1"

    val defaultModel: String get() = "google/gemma-4-26b-a4b-it:free"

    val isDevelopment: Boolean get() = System.getenv("ENVIRONMENT") != "production"
}
