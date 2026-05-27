package com.example.data.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object BookingTable : Table("bookings") {

    val id = integer("id").autoIncrement()

    val coachId = reference("coach_id", CoachTable.id)

    val workoutId = reference("workout_id", WorkoutTable.id)

    val slots = integer("slots")

    val name = varchar("name", 255)

    val extra = varchar("extra", 255).nullable()

    val time = datetime("time")

    override val primaryKey = PrimaryKey(id)
}