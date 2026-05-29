package com.example.data.database

import com.example.data.seed.UserTypeSeeder
import com.example.data.seed.UserSeeder
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

import com.example.data.tables.*
import data.tables.BookingClientTable

object DatabaseFactory {

    fun init(environment: ApplicationEnvironment) {

        val config = environment.config

        val url = config.property("database.url").getString()
        val user = config.property("database.user").getString()
        val password = config.property("database.password").getString()
        val driver = config.property("database.driver").getString()

        Database.connect(
            url = url,
            driver = driver,
            user = user,
            password = password
        )

        transaction {
            SchemaUtils.create(
                UserTypesTable,
                UserTable,
                CoachTypeTable,
                CoachTable,
                WorkoutTable,
                ClientTable,
                BookingTable,
                BookingClientTable
            )
        }

        UserTypeSeeder.seed()
    }
}