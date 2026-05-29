package domain.usecase

import domain.model.Booking
import domain.repository.BookingRepository

class UpdateBookingUseCase(private val repository: BookingRepository) {
    operator fun invoke(booking: Booking): Boolean = repository.update(booking)
}
