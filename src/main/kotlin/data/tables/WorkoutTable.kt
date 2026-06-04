package com.example.data.tables

import org.jetbrains.exposed.sql.Table

object WorkoutTable : Table("workouts") {

    val id = integer("id").autoIncrement()

    val coachTypeId = reference("coach_type_id", CoachTypeTable.id)

    val name = varchar("name", 255)

    val description = varchar("description", 255).nullable()

    val duration = integer("duration") 

    override val primaryKey = PrimaryKey(id)
}