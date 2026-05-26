package com.example.data.seed

import com.example.data.tables.UserTypesTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object UserTypeSeeder {

    fun seed() = transaction {

        val existing = UserTypesTable.selectAll().map { it[UserTypesTable.name] }

        val defaults = listOf("ADMIN", "COACH", "CLIENT")

        defaults.forEach { type ->
            if (type !in existing) {
                UserTypesTable.insert {
                    it[name] = type
                }
            }
        }
    }
}