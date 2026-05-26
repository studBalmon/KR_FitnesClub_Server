package domain.usecase

import domain.repository.WorkoutRepository

class GetAllWorkoutsUseCase(
    private val repository: WorkoutRepository
) {
    operator fun invoke() = repository.getAll()
}