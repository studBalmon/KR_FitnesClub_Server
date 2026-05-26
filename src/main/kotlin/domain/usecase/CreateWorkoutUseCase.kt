package domain.usecase

import domain.model.Workout
import domain.repository.WorkoutRepository

class CreateWorkoutUseCase(
    private val repository: WorkoutRepository
) {
    operator fun invoke(workout: Workout): Int {
        return repository.create(workout)
    }
}