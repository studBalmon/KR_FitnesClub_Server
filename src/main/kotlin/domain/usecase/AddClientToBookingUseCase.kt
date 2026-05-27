package domain.usecase

import domain.repository.BookingRepository

class AddClientToBookingUseCase(
    private val repository: BookingRepository
) {
    operator fun invoke(bookingId: Int, clientId: Int): Boolean {
        return repository.addClientToBooking(bookingId, clientId)
    }
}