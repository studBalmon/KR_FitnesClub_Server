package com.example.data.tables

import org.jetbrains.exposed.sql.Table

object CoachTable : Table("coaches") {

    val id = integer("id").autoIncrement()

    val userId = reference("user_id", UserTable.id)

    val coachTypeId = reference("coach_type_id", CoachTypeTable.id)

    override val primaryKey = PrimaryKey(id)
}