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

        val url = environment.config.property("database.url").getString()
        val driver = environment.config.property("database.driver").getString()
        val user = environment.config.property("database.user").getString()
        val password = environment.config.property("database.password").getString()

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