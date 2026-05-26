package domain.repository

import domain.model.Workout

interface WorkoutRepository {

    fun create(workout: Workout): Int

    fun getAll(): List<Workout>

    fun getById(id: Int): Workout?

    fun update(id: Int, workout: Workout): Boolean

    fun delete(id: Int): Boolean
}