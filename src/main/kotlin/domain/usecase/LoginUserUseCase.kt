package domain.usecase

import domain.repository.UserRepository
import domain.model.User
import com.example.util.PasswordHasher

class LoginUserUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(email: String, password: String): User? {
        val user = repository.findByEmail(email) ?: return null

        val isValid = PasswordHasher.verify(
            password,
            user.passwordHash
        )

        return if (isValid) user else null
    }
}