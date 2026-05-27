package domain.usecase

import com.example.util.PasswordHasher
import domain.repository.UserRepository
import domain.model.User

data class LoginResult(
    val user: User,
    val role: String
)

class LoginUserUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(email: String, password: String): LoginResult? {

        val user = repository.findByEmail(email) ?: return null

        val isValid = PasswordHasher.verify(
            password,
            user.passwordHash
        )

        if (!isValid) return null

        val role = repository.getRoleByUserId(user.id)

        return LoginResult(
            user = user,
            role = role
        )
    }
}