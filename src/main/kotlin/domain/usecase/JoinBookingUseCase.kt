package domain.usecase

import domain.repository.BookingRepository

class JoinBookingUseCase(
    private val repository: BookingRepository
) {
    operator fun invoke(bookingId: Int, clientId: Int): Boolean {
        return repository.addClientToBooking(bookingId, clientId)
    }
}