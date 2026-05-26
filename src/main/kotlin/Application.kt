package com.example

import io.ktor.server.application.*
import com.example.data.database.DatabaseFactory
import plugins.di.appModule
import presentation.routes.configureRouting
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import plugins.configureSecurity
import plugins.configureSerialization
import plugins.configureStatusPages

fun Application.module() {

    DatabaseFactory.init(environment)

    configureSerialization()
    configureSecurity()
    configureStatusPages()

    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }

    configureRouting()
}