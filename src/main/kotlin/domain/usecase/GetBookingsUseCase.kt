package domain.usecase

import domain.repository.BookingRepository

class GetBookingsUseCase(
    private val repository: BookingRepository
) {
    operator fun invoke() = repository.getAll()
}