package data.repository

import com.example.data.tables.WorkoutTable
import domain.model.Workout
import domain.repository.WorkoutRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class WorkoutRepositoryImpl : WorkoutRepository {

    override fun create(workout: Workout): Int = transaction {
        WorkoutTable.insert {
            it[coachTypeId] = workout.coachTypeId
            it[name] = workout.name
            it[description] = workout.description
            it[duration] = workout.duration
        } get WorkoutTable.id
    }

    override fun getAll(): List<Workout> = transaction {
        WorkoutTable.selectAll().map { it.toWorkout() }
    }

    override fun getById(id: Int): Workout? = transaction {
        WorkoutTable.selectAll().where { WorkoutTable.id eq id }
            .map { it.toWorkout() }
            .singleOrNull()
    }

    override fun update(id: Int, workout: Workout): Boolean = transaction {
        WorkoutTable.update({ WorkoutTable.id eq id }) {
            it[coachTypeId] = workout.coachTypeId
            it[name] = workout.name
            it[description] = workout.description
            it[duration] = workout.duration
        } > 0
    }

    override fun delete(id: Int): Boolean = transaction {
        WorkoutTable.deleteWhere { WorkoutTable.id eq id } > 0
    }

    private fun ResultRow.toWorkout() = Workout(
        id = this[WorkoutTable.id],
        coachTypeId = this[WorkoutTable.coachTypeId],
        name = this[WorkoutTable.name],
        description = this[WorkoutTable.description],
        duration = this[WorkoutTable.duration]
    )

    override fun searchByName(query: String): List<Workout> = transaction {

        val pattern = "%${query.lowercase()}%"

        WorkoutTable
            .selectAll()
            .where { WorkoutTable.name.lowerCase() like pattern }
            .map { it.toWorkout() }
    }
}