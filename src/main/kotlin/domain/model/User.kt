package domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val fio: String,
    val phone: String,
    val email: String,
    val userTypeId: Int,
    val passwordHash: String
)