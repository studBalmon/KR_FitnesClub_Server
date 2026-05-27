package domain.usecase

import domain.model.Booking
import domain.repository.BookingRepository

class CreateBookingUseCase(
    private val repository: BookingRepository
) {
    operator fun invoke(booking: Booking): Int {
        return repository.create(booking)
    }
}