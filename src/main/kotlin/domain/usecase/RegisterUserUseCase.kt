package domain.usecase

import domain.repository.UserRepository
import domain.model.User
import domain.repository.ClientRepository
import domain.repository.CoachRepository

class RegisterUserUseCase(
    private val repository: UserRepository,
    private val clientRepository: ClientRepository
) {
    operator fun invoke(
        email: String,
        password: String,
        fio: String,
        phone: String,
        userTypeId: Int
    ) {
        val user = User(
            id = 0,
            fio = fio,
            phone = phone,
            email = email,
            userTypeId = userTypeId,
            passwordHash = password
        )

        val userId = repository.create(user)
        val role = repository.getRoleByUserId(userId)
        if (role == "CLIENT") {
            clientRepository.create(userId)
        }
    }
}