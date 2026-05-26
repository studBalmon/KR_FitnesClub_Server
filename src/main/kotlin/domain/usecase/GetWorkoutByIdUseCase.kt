package domain.usecase

import domain.repository.WorkoutRepository

class GetWorkoutByIdUseCase(
    private val repository: WorkoutRepository
) {
    operator fun invoke(id: Int) = repository.getById(id)
}