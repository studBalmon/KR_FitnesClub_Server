package domain.usecase

import domain.repository.UserRepository
import domain.model.User

class RegisterUserUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(
        email: String,
        password: String,
        fio: String,
        phone: String
    ) {
        val user = User(
            id = 0,
            fio = fio,
            phone = phone,
            email = email,
            userTypeId = 1,
            passwordHash = password
        )

        repository.create(user)
    }
}