package domain.usecase

import domain.repository.BookingRepository

class RemoveClientFromAllBookingsUseCase(
    private val repository: BookingRepository
) {
    operator fun invoke(clientId: Int): Int = repository.removeAllByClient(clientId)
}
