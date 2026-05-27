package domain.usecase

import domain.repository.CoachRepository

class GetCoachByUserIdUseCase(
    private val repository: CoachRepository
) {
    operator fun invoke(userId: Int): Int? =
        repository.getByUserId(userId)
}