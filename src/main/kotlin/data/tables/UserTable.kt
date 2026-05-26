package com.example.data.tables

import org.jetbrains.exposed.sql.Table

object UserTable : Table("users") {

    val id = integer("id").autoIncrement()

    val fio = varchar("fio", 255)

    val phone = varchar("phone", 20).uniqueIndex()

    val email = varchar("email", 255).uniqueIndex()

    val userTypeId = reference("user_type_id", UserTypesTable.id)

    val passwordHash = varchar("password_hash", 255)

    override val primaryKey = PrimaryKey(id)
}