package presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val fio: String,
    val phone: String,
    val userTypeId: Int
)