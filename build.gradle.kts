plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
}

group = "com.personalaiserver"
version = "1.0.0"

application {
    mainClass.set("com.personalaiserver.ApplicationKt")
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.status.pages)

    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.java.time)

    implementation(libs.postgresql)
    implementation(libs.hikaricp)

    implementation(libs.logback.classic)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    testImplementation("io.ktor:ktor-server-test-host:${libs.versions.ktor.get()}")
    testImplementation("org.jetbrains.kotlin:kotlin-test:${libs.versions.kotlin.get()}")
}

ktor {
    fatJar {
        archiveFileName.set("personal-ai-server.jar")
    }
}
