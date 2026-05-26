package domain.model

data class User(
    val id: Int,
    val fio: String,
    val phone: String,
    val email: String,
    val userTypeId: Int,
    val passwordHash: String
)