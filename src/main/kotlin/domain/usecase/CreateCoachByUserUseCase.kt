package domain.usecase

import domain.model.User
import domain.model.Workout
import domain.repository.CoachRepository
import domain.repository.UserRepository
import domain.repository.WorkoutRepository

class CreateCoachByUserUseCase(
    private val coachRepository: CoachRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(
        email: String,
        password: String,
        fio: String,
        phone: String,
        userTypeId: Int,
        coachTypeId: Int
    ) {
        val user = User(
            id = 0,
            fio = fio,
            phone = phone,
            email = email,
            userTypeId = userTypeId,
            passwordHash = password
        )

        val userId = userRepository.create(user)

        coachRepository.create(userId, coachTypeId)
    }
}