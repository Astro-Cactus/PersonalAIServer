package com.personalaiserver.database

import com.personalaiserver.config.AppConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("DatabaseFactory")

object DatabaseFactory {
    private var dataSource: HikariDataSource? = null

    fun init(appConfig: AppConfig) {
        val config = HikariConfig().apply {
            jdbcUrl = appConfig.databaseUrl
            driverClassName = "org.postgresql.Driver"
            username = appConfig.databaseUser
            password = appConfig.databasePassword
            maximumPoolSize = 10
            minimumIdle = 2
            idleTimeout = 30000
            maxLifetime = 600000
            connectionTimeout = 10000
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        dataSource = HikariDataSource(config)
        Database.connect(dataSource!!)

        log.info("Database connected successfully")
    }

    fun close() {
        dataSource?.close()
    }
}
