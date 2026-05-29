package domain.usecase

import domain.repository.WorkoutRepository

class SearchWorkoutsByNameUseCase(
    private val repository: WorkoutRepository
) {
    operator fun invoke(query: String) =
        repository.searchByName(query)
}