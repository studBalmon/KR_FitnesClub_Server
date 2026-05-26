package com.example.data.tables

import org.jetbrains.exposed.sql.Table

object CoachTypeTable : Table("coach_types") {

    val id = integer("id").autoIncrement()

    val name = varchar("name", 50).uniqueIndex()

    override val primaryKey = PrimaryKey(id)
}