package domain.usecase

import domain.repository.BookingRepository

class GetBookingsByCoachUseCase(
    private val repository: BookingRepository
) {
    operator fun invoke(coachId: Int) =
        repository.getByCoach(coachId)
}