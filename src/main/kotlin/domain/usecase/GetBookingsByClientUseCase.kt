package domain.usecase

import domain.repository.BookingRepository

class GetBookingsByClientUseCase(
    private val repository: BookingRepository
) {
    operator fun invoke(clientId: Int) = repository.getByClient(clientId)
}