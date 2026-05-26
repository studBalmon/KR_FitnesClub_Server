package presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutRequest(
    val coachTypeId: Int,
    val name: String,
    val description: String?,
    val duration: Int
)