package domain.usecase

import domain.repository.BookingRepository

class DeleteBookingUseCase(
    private val repository: BookingRepository
) {
    operator fun invoke(id: Int) = repository.delete(id)
}