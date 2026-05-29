package domain.usecase

import domain.model.Booking
import domain.repository.BookingRepository

class GetBookingByIdUseCase(private val repository: BookingRepository) {
    operator fun invoke(id: Int): Booking? = repository.getById(id)
}
