package com.example.data.seed

import com.example.data.tables.UserTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object UserSeeder{

    fun seed() = transaction {

        val existing = UserTable.selectAll().map { it[UserTable.phone] }

        val defaults = listOf("+71111111111")

        defaults.forEach { type ->
            if (type !in existing) {
                UserTable.insert {
                    val fio = "Тестовый Тест Тестович"
                    val phone = "+71111111111"
                    val email = "Тест@example.com"
                    val userTypeId = "2"
                    val passwordHash = "\$2a\$10\$lR89sepI/zjQvZjL.H6yxuSSpvxZ8kZGpKll6h8xn6gnw18gaf0XG"
                }
            }
        }
    }
}