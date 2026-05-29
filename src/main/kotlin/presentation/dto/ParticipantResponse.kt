package presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class ParticipantResponse(
    val fio: String,
    val phone: String
)
