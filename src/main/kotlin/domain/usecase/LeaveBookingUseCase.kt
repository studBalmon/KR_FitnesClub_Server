package domain.usecase

import domain.repository.BookingRepository

class LeaveBookingUseCase(private val repository: BookingRepository) {
    operator fun invoke(bookingId: Int, clientId: Int): Boolean =
        repository.removeClientFromBooking(bookingId, clientId)
}
