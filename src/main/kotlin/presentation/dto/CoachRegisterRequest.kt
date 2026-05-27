package presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoachRegisterRequest(
    val email: String,
    val password: String,
    val fio: String,
    val phone: String,
    val userTypeId: Int,
    val coachTypeId: Int
)