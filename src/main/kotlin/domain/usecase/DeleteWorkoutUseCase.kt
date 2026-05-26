package domain.usecase

import domain.repository.WorkoutRepository

class DeleteWorkoutUseCase(
    private val repository: WorkoutRepository
) {
    operator fun invoke(id: Int) = repository.delete(id)
}