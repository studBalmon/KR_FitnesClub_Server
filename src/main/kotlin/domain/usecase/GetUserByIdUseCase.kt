package domain.usecase

import domain.repository.UserRepository

class GetUserByIdUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(id: Int) =
        repository.getById(id)
}