package presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    val fio: String,
    val phone: String,
    val email: String
)
