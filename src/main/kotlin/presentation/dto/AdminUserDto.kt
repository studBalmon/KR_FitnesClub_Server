package presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class AdminUserResponse(
    val id: Int,
    val fio: String,
    val phone: String,
    val email: String,
    val userTypeId: Int,
    val roleName: String,
    val coachTypeId: Int?
)

@Serializable
data class AdminCreateUserRequest(
    val fio: String,
    val phone: String,
    val email: String,
    val password: String,
    val userTypeId: Int,
    val coachTypeId: Int? = null
)

@Serializable
data class AdminUpdateUserRequest(
    val fio: String,
    val phone: String,
    val email: String,
    val newPassword: String? = null,
    val coachTypeId: Int? = null   // для тренеров
)

@Serializable
data class CoachTypeResponse(
    val id: Int,
    val name: String
)

@Serializable
data class CoachTypeRequest(val name: String)
