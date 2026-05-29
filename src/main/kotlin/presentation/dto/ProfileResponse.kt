package presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val id: Int,
    val fio: String,
    val phone: String,
    val email: String,
    val cardEndDate: String?,  // null если не CLIENT
    val userTypeId: Int        // 2=COACH, 3=CLIENT
)
