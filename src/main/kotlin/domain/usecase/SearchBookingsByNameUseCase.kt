package domain.usecase

import domain.repository.BookingRepository

class SearchBookingsByNameUseCase(
    private val repository: BookingRepository
) {
    operator fun invoke(query: String) =
        repository.searchByName(query)
}