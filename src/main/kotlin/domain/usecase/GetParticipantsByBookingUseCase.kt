package domain.usecase

import domain.repository.BookingRepository
import domain.repository.ClientRepository
import domain.repository.UserRepository
import presentation.dto.ParticipantResponse

class GetParticipantsByBookingUseCase(
    private val bookingRepository: BookingRepository,
    private val clientRepository: ClientRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(bookingId: Int): List<ParticipantResponse> {
        val booking = bookingRepository.getById(bookingId) ?: return emptyList()
        return booking.clients.mapNotNull { clientId ->
            val client = clientRepository.getById(clientId) ?: return@mapNotNull null
            val user = userRepository.getById(client.userId) ?: return@mapNotNull null
            ParticipantResponse(fio = user.fio, phone = user.phone)
        }
    }
}
