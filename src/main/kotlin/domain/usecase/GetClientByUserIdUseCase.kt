package domain.usecase

import domain.repository.ClientRepository

class GetClientByUserIdUseCase(
    private val repository: ClientRepository
) {
    operator fun invoke(userId: Int): Int? {
        return repository.getByUserId(userId)?.id
    }
}