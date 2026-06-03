package presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoachResponse(
    val id: Int,
    val fio: String,
    val coachTypeName: String? = null
)
